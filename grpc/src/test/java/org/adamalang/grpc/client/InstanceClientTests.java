package org.adamalang.grpc.client;

import org.adamalang.grpc.TestBed;
import org.adamalang.grpc.client.contracts.AskAttachmentCallback;
import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.client.contracts.SeqCallback;
import org.adamalang.grpc.mocks.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceClientTests {
    @Test
    public void stubPersistent() throws Exception {
        try (TestBed bed = new TestBed(10001, "@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
            MockClentLifecycle lifecycle = new MockClentLifecycle();
            try (InstanceClient client = new InstanceClient(bed.identity, "127.0.0.1:10001", bed.clientExecutor, lifecycle, new StdErrLogger())) {
                {
                    AssertCreateFailure failure = new AssertCreateFailure();
                    client.create("nope", "nope", "space", "1", null, "{}", failure);
                    Assert.assertFalse(client.ping(2500));
                    failure.await(723982);
                }
                bed.startServer();
                Assert.assertTrue(client.ping(5000));
                {
                    AssertCreateSuccess success = new AssertCreateSuccess();
                    client.create("nope", "nope", "space", "2", "123", "{}", success);
                    success.await();
                }
                bed.stopServer();
                Assert.assertFalse(client.ping(5000));
                {
                    AssertCreateFailure failure = new AssertCreateFailure();
                    client.create("nope", "nope", "space", "3", "42", "{}", failure);
                    failure.await(723982);
                }
                bed.startServer();
                Assert.assertTrue(client.ping(5000));
                {
                    AssertCreateSuccess success = new AssertCreateSuccess();
                    client.create("nope", "nope", "space", "4", null, "{}", success);
                    success.await();
                }
            }
        }
    }

    @Test
    public void multiplexPersistent() throws Exception {
        try (TestBed bed = new TestBed(10002, "@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.1; } #p { x++; } ")) {
            bed.startServer();
            MockClentLifecycle lifecycle = new MockClentLifecycle();
            MockEvents events = new MockEvents();
            Runnable happy = events.latchAt(5);
            Runnable disconnect = events.latchAt(6);
            Runnable reconnect = events.latchAt(8);
            Runnable disconnectAgain = events.latchAt(9);
            AtomicBoolean created = new AtomicBoolean(false);
            try (InstanceClient client = new InstanceClient(bed.identity, "127.0.0.1:10002", bed.clientExecutor, new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                    System.err.println("connected");
                    if (created.compareAndExchange(false, true) == false) {
                        AssertCreateSuccess success = new AssertCreateSuccess();
                        client.create("nope", "nope", "space", "1", "123", "{}", success);
                        success.await();
                    }
                    client.connect("nope", "test", "space", "1", events);
                    lifecycle.connected(client);
                }

                @Override
                public void disconnected(InstanceClient client) {
                    lifecycle.disconnected(client);
                }
            }, (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
            })) {
                happy.run();
                bed.stopServer();
                disconnect.run();
                bed.startServer();
                reconnect.run();
                bed.stopServer();
                disconnectAgain.run();
                events.assertWrite(0, "CONNECTED");
                events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
                events.assertWrite(2, "DELTA:{\"data\":{\"x\":124},\"seq\":5}");
                events.assertWrite(3, "DELTA:{\"seq\":6}");
                events.assertWrite(4, "DELTA:{\"seq\":7}");
                events.assertWrite(5, "DISCONNECTED");
                events.assertWrite(6, "CONNECTED");
                events.assertWrite(7, "DELTA:{\"data\":{\"x\":124},\"seq\":12}");
                events.assertWrite(8, "DISCONNECTED");
                Assert.assertEquals("CDCD", lifecycle.toString());
            }
        }
    }

    @Test
    public void sendAndDisconnect() throws Exception {
        try (TestBed bed = new TestBed(10003, "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
            bed.startServer();
            MockClentLifecycle lifecycle = new MockClentLifecycle();
            MockEvents events = new MockEvents() {
                @Override
                public void connected(InstanceClient.Remote remote) {
                    remote.send("foo", null, "{\"z\":\"100\"}", new SeqCallback() {
                        @Override
                        public void success(int seq) {
                            remote.disconnect();
                        }

                        @Override
                        public void error(int code) {
                            System.err.println("error!:" + code);
                        }
                    });
                    super.connected(remote);
                }
            };
            Runnable happy = events.latchAt(4);
            try (InstanceClient client = new InstanceClient(bed.identity, "127.0.0.1:10003", bed.clientExecutor, new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                    AssertCreateSuccess success = new AssertCreateSuccess();
                    client.create("nope", "nope", "space", "1", "123", "{}", success);
                    success.await();
                    client.connect("nope", "test", "space", "1", events);
                    lifecycle.connected(client);
                }

                @Override
                public void disconnected(InstanceClient client) {
                    lifecycle.disconnected(client);
                }
            }, (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
            })) {
                bed.startServer();
                happy.run();
                events.assertWrite(0, "CONNECTED");
                events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
                events.assertWrite(2, "DELTA:{\"data\":{\"x\":223},\"seq\":6}");
                events.assertWrite(3, "DISCONNECTED");
            }
        }
    }

    @Test
    public void disconnectThenSendFailure() throws Exception {
        try (TestBed bed = new TestBed(10004, "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
            bed.startServer();
            MockClentLifecycle lifecycle = new MockClentLifecycle();
            AtomicInteger errorCodeSeq = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(1);
            MockEvents events = new MockEvents() {
                InstanceClient.Remote remote = null;
                @Override
                public void connected(InstanceClient.Remote remote) {
                    this.remote = remote;
                    remote.disconnect();
                    super.connected(remote);
                }

                @Override
                public void disconnected() {
                    remote.send("foo", "marker1", "{\"z\":\"100\"}", new SeqCallback() {
                        @Override
                        public void success(int seq) {
                        }

                        @Override
                        public void error(int code) {
                            errorCodeSeq.set(code);
                            latch.countDown();
                        }
                    });
                    super.disconnected();
                }
            };
            Runnable happy = events.latchAt(3);
            try (InstanceClient client = new InstanceClient(bed.identity, "127.0.0.1:10004", bed.clientExecutor, new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                    AssertCreateSuccess success = new AssertCreateSuccess();
                    client.create("nope", "nope", "space", "1", "123", "{}", success);
                    success.await();
                    client.connect("nope", "test", "space", "1", events);
                    lifecycle.connected(client);
                }

                @Override
                public void disconnected(InstanceClient client) {
                    lifecycle.disconnected(client);
                }
            }, (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
            })) {
                bed.startServer();
                happy.run();
                Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
                events.assertWrite(0, "CONNECTED");
                events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
                events.assertWrite(2, "DISCONNECTED");
                Assert.assertEquals(798735, errorCodeSeq.get());
            }
        }
    }

    @Test
    public void cantAttachPolicy() throws Exception {
        try (TestBed bed = new TestBed(10005, "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
            bed.startServer();
            MockClentLifecycle lifecycle = new MockClentLifecycle();
            CountDownLatch cantAttachLatch = new CountDownLatch(1);
            MockEvents events = new MockEvents() {
                @Override
                public void connected(InstanceClient.Remote remote) {
                    remote.canAttach(new AskAttachmentCallback() {
                        @Override
                        public void allow() {

                        }

                        @Override
                        public void reject() {
                            remote.disconnect();
                            cantAttachLatch.countDown();
                        }

                        @Override
                        public void error(int code) {

                        }
                    });
                    super.connected(remote);
                }
            };
            Runnable happy = events.latchAt(3);
            try (InstanceClient client = new InstanceClient(bed.identity, "127.0.0.1:10005", bed.clientExecutor, new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                    AssertCreateSuccess success = new AssertCreateSuccess();
                    client.create("nope", "nope", "space", "1", "123", "{}", success);
                    success.await();
                    client.connect("nope", "test", "space", "1", events);
                    lifecycle.connected(client);
                }

                @Override
                public void disconnected(InstanceClient client) {
                    lifecycle.disconnected(client);
                }
            }, (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
            })) {
                bed.startServer();
                happy.run();
                Assert.assertTrue(cantAttachLatch.await(2000, TimeUnit.MILLISECONDS));
                events.assertWrite(0, "CONNECTED");
                events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
                events.assertWrite(2, "DISCONNECTED");
            }
        }
    }

    @Test
    public void canAttachThenAttach() throws Exception {
        try (TestBed bed = new TestBed(10006, "@connected(who) { return true; } public int x; @construct { x = 123; } @can_attach(who) { return true; } @attached (who, what) { x++; } ")) {
            bed.startServer();
            MockClentLifecycle lifecycle = new MockClentLifecycle();
            CountDownLatch canAttachLatch = new CountDownLatch(1);
            MockEvents events = new MockEvents() {
                @Override
                public void connected(InstanceClient.Remote remote) {
                    remote.canAttach(new AskAttachmentCallback() {
                        @Override
                        public void allow() {
                            remote.attach("id", "name", "text/json", 42, "x", "y", new SeqCallback() {
                                @Override
                                public void success(int seq) {
                                    remote.disconnect();
                                    canAttachLatch.countDown();
                                }

                                @Override
                                public void error(int code) {

                                }
                            });
                        }

                        @Override
                        public void reject() {
                        }

                        @Override
                        public void error(int code) {

                        }
                    });
                    super.connected(remote);
                }
            };
            Runnable happy = events.latchAt(4);
            try (InstanceClient client = new InstanceClient(bed.identity, "127.0.0.1:10006", bed.clientExecutor, new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                    AssertCreateSuccess success = new AssertCreateSuccess();
                    client.create("nope", "nope", "space", "1", "123", "{}", success);
                    success.await();
                    client.connect("nope", "test", "space", "1", events);
                    lifecycle.connected(client);
                }

                @Override
                public void disconnected(InstanceClient client) {
                    lifecycle.disconnected(client);
                }
            }, (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
            })) {
                bed.startServer();
                happy.run();
                Assert.assertTrue(canAttachLatch.await(2000, TimeUnit.MILLISECONDS));
                events.assertWrite(0, "CONNECTED");
                events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
                events.assertWrite(2, "DELTA:{\"data\":{\"x\":124},\"seq\":6}");
                events.assertWrite(3, "DISCONNECTED");
            }
        }
    }

    @Test
    public void cantAttachDisconnect() throws Exception {
        try (TestBed bed = new TestBed(10007, "@connected(who) { return true; } public int x; @construct { x = 123; } message Y { int z; } channel foo(Y y) { x += y.z; }")) {
            bed.startServer();
            MockClentLifecycle lifecycle = new MockClentLifecycle();
            CountDownLatch cantAttachLatch = new CountDownLatch(1);
            AtomicInteger error = new AtomicInteger(0);
            MockEvents events = new MockEvents() {
                InstanceClient.Remote remote;

                @Override
                public void connected(InstanceClient.Remote remote) {
                    this.remote = remote;
                    super.connected(remote);
                }

                @Override
                public void delta(String data) {
                    this.remote.disconnect();
                    super.delta(data);
                }

                @Override
                public void disconnected() {
                    remote.canAttach(new AskAttachmentCallback() {
                        @Override
                        public void allow() {
                        }

                        @Override
                        public void reject() {
                        }

                        @Override
                        public void error(int code) {
                            error.set(code);
                            cantAttachLatch.countDown();
                        }
                    });
                    super.disconnected();
                }
            };
            Runnable happy = events.latchAt(3);
            try (InstanceClient client = new InstanceClient(bed.identity, "127.0.0.1:10007", bed.clientExecutor, new Lifecycle() {
                @Override
                public void connected(InstanceClient client) {
                    AssertCreateSuccess success = new AssertCreateSuccess();
                    client.create("nope", "nope", "space", "1", "123", "{}", success);
                    success.await();
                    client.connect("nope", "test", "space", "1", events);
                    lifecycle.connected(client);
                }

                @Override
                public void disconnected(InstanceClient client) {
                    lifecycle.disconnected(client);
                }
            }, (t, errorCode) -> {
                System.err.println("EXCEPTION:" + t.getMessage());
            })) {
                bed.startServer();
                happy.run();
                Assert.assertTrue(cantAttachLatch.await(2000, TimeUnit.MILLISECONDS));
                Assert.assertEquals(798735, error.get());
                events.assertWrite(0, "CONNECTED");
                events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
                events.assertWrite(2, "DISCONNECTED");
            }
        }
    }

}
