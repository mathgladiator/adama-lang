/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.net;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class NetSuiteTests {

  @Test
  public void happy() throws Exception {
    NetBase base = new NetBase(new NetMetrics(new NoOpMetricsFactory()), identity(), 2, 4);
    try {
      ServerHandle handle = base.serve(25001, new Handler() {
        @Override
        public ByteStream create(ByteStream upstream) {
          return new ByteStream() {
            @Override
            public void request(int bytes) {
              System.err.println("server|Request=" + bytes);
              upstream.request(bytes * 2);
            }

            @Override
            public ByteBuf create(int bestGuessForSize) { // Not used on server side
              return null;
            }

            @Override
            public void next(ByteBuf buf) {
              int val = buf.readIntLE();
              System.err.println("Server|Data=" + val);
              ByteBuf toSend = upstream.create(4);
              toSend.writeIntLE(val);
              upstream.next(toSend);
            }

            @Override
            public void completed() {
              System.err.println("server|Done");
            }

            @Override
            public void error(int errorCode) {
              System.err.println("server|Error:" + errorCode);
            }
          };
        }
      });
      try {
        AtomicReference<ChannelClient> theClient = new AtomicReference<>();
        CountDownLatch phases = new CountDownLatch(102);
        Thread thread = new Thread(() -> handle.waitForEnd());
        thread.start();
        System.err.println("Server running");
        base.connect("127.0.0.1:25001", new Lifecycle() {
          int attemptsLeft = 3;

          @Override
          public void connected(ChannelClient channel) {
            theClient.set(channel);
            System.err.println("client connected");
            channel.open(new ByteStream() {
              @Override
              public void request(int bytes) {
                System.err.println("client|Request=" + bytes);
                phases.countDown();
              }

              @Override
              public ByteBuf create(int bestGuessForSize) {
                return null;
              }

              @Override
              public void next(ByteBuf buf) {
                int value = buf.readIntLE();
                System.err.println("client|Data=" + value);
                phases.countDown();
              }

              @Override
              public void completed() {
                System.err.println("Completed");
              }

              @Override
              public void error(int errorCode) {
                System.err.println("Client error:" + +errorCode);
              }
            }, new Callback<ByteStream>() {
              @Override
              public void success(ByteStream value) {
                value.request(1000);
                for (int k = 0; k < 100; k++) {
                  ByteBuf toSend = value.create(4);
                  toSend.writeIntLE(42 + k);
                  value.next(toSend);
                }
                value.completed();
                phases.countDown(); // GOT CONNECTED
              }

              @Override
              public void failure(ErrorCodeException ex) {
                ex.printStackTrace();
              }
            });
          }

          @Override
          public void failed(ErrorCodeException ex) {
            if (attemptsLeft-- > 0) {
              base.connect("127.0.0.1:25001", this);
            }
          }

          @Override
          public void disconnected() {

          }
        });
        Assert.assertTrue(phases.await(2000, TimeUnit.MILLISECONDS));
        theClient.get().close();
      } finally {
        handle.kill();
      }
    } finally {
      base.shutdown();
      CountDownLatch latchFailed = new CountDownLatch(1);
      base.connect("127.0.0.1:25001", new Lifecycle() {
        @Override
        public void connected(ChannelClient channel) {

        }

        @Override
        public void failed(ErrorCodeException ex) {
          latchFailed.countDown();
        }

        @Override
        public void disconnected() {

        }
      });
      Assert.assertTrue(latchFailed.await(1000, TimeUnit.MILLISECONDS));
    }
    base.waitForShutdown();
  }

  public static MachineIdentity identity() throws Exception {
    for (String search : new String[]{"./", "../", "./grpc/"}) {
      String candidate = search + "localhost.identity";
      File file = new File(candidate);
      if (file.exists()) {
        return MachineIdentity.fromFile(candidate);
      }
    }
    throw new NullPointerException("could not find identity.localhost");
  }

  @Test
  public void sad_remote_error() throws Exception {
    NetBase base = new NetBase(new NetMetrics(new NoOpMetricsFactory()), identity(), 2, 4);
    try {
      ServerHandle handle = base.serve(25002, upstream -> new ByteStream() {
        @Override
        public void request(int bytes) {
          System.err.println("server|Request=" + bytes);
          upstream.request(bytes * 2);
        }

        @Override
        public ByteBuf create(int bestGuessForSize) { // Not used on server side
          return null;
        }

        @Override
        public void next(ByteBuf buf) {
          int val = buf.readIntLE();
          System.err.println("Server|Data=" + val);
          if (val == 50) {
            upstream.error(7209550);
          } else {
            ByteBuf toSend = upstream.create(4);
            toSend.writeIntLE(val);
            upstream.next(toSend);
          }
        }

        @Override
        public void completed() {
          System.err.println("server|Done");
        }

        @Override
        public void error(int errorCode) {
          System.err.println("server|Error:" + errorCode);
        }
      });
      try {
        CountDownLatch phases = new CountDownLatch(10);
        Thread thread = new Thread(() -> handle.waitForEnd());
        thread.start();
        System.err.println("Server running");
        base.connect("127.0.0.1:25002", new Lifecycle() {
          int attemptsLeft = 3;

          @Override
          public void connected(ChannelClient channel) {
            System.err.println("client connected");
            channel.open(new ByteStream() {
              @Override
              public void request(int bytes) {
                System.err.println("client|Request=" + bytes);
                phases.countDown();
              }

              @Override
              public ByteBuf create(int bestGuessForSize) {
                return null;
              }

              @Override
              public void next(ByteBuf buf) {
                int value = buf.readIntLE();
                System.err.println("client|Data=" + value);
                phases.countDown();
              }

              @Override
              public void completed() {
                System.err.println("Completed");
              }

              @Override
              public void error(int errorCode) {
                System.err.println("Client error:" + +errorCode);
                phases.countDown();
              }
            }, new Callback<ByteStream>() {
              @Override
              public void success(ByteStream value) {
                value.request(1000);
                for (int k = 0; k < 100; k++) {
                  ByteBuf toSend = value.create(4);
                  toSend.writeIntLE(42 + k);
                  value.next(toSend);
                }
                value.completed();
                phases.countDown(); // GOT CONNECTED
              }

              @Override
              public void failure(ErrorCodeException ex) {

              }
            });
          }

          @Override
          public void failed(ErrorCodeException ex) {
            if (attemptsLeft-- > 0) {
              base.connect("127.0.0.1:25001", this);
            }
          }

          @Override
          public void disconnected() {

          }
        });
        Assert.assertTrue(phases.await(2000, TimeUnit.MILLISECONDS));
      } finally {
        handle.kill();
      }
    } finally {
      base.shutdown();
    }
    base.waitForShutdown();
  }
}
