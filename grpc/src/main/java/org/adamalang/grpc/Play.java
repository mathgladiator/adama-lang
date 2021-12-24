package org.adamalang.grpc;

import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.client.contracts.Remote;
import org.adamalang.grpc.client.contracts.Events;
import org.adamalang.grpc.client.contracts.CreateCallback;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.grpc.server.Server;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.CoreService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Play {
    public static void main(String[] args) throws Exception {
        DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; transition #p in 1; }\"},\"default\":\"x\"}", (t, errorCode) -> {

        });
        DeploymentFactoryBase base = new DeploymentFactoryBase();
        base.deploy("space", plan);

        ScheduledExecutorService inMemoryThread = Executors.newSingleThreadScheduledExecutor();

        CoreService service = new CoreService(base, //
                new InMemoryDataService(inMemoryThread, TimeSource.REAL_TIME), //
                TimeSource.REAL_TIME, 2);

        MachineIdentity identity = MachineIdentity.fromFile("./grpc/localhost.identity");

        Server server = new Server(identity, service, 2321);
        server.start();

        InstanceClient instanceClient = new InstanceClient(identity, "127.0.0.1:2321", inMemoryThread, new Lifecycle() {
            @Override
            public void connected(InstanceClient client) {
                System.err.println("Client connected");
            }

            @Override
            public void disconnected(InstanceClient client) {
                System.err.println("Client disconnected");
            }
        }, (t, errorCode) -> {
            t.printStackTrace();
        });
        System.err.println("Ping now!");
        instanceClient.ping(1000);
        System.err.println("Ping!");

        instanceClient.create("me", "life", "space", "123", null, "{}", new CreateCallback() {
            @Override
            public void created() {
                System.err.println("Created!");
                instanceClient.connect("me", "life", "space", "123", new Events() {
                    @Override
                    public void delta(String data) {
                        System.err.println("DELTA:" + data);
                    }

                    @Override
                    public void connected(Remote remote) {
                        System.err.println("CONNECTED, yay!");
                    }

                    @Override
                    public void disconnected() {
                        System.err.println("DISCONNECTED");
                    }

                    @Override
                    public void error(int code) {
                        System.err.println("ERROR:" + code);
                    }
                });
            }

            @Override
            public void error(int code) {
                System.err.println("ERROR:" + code);
            }
        });



        for (int k = 0; k < 10; k++) {
            System.err.print(".");
            Thread.sleep(1000);
        }
        instanceClient.close();
        server.close();
        service.shutdown();
        inMemoryThread.shutdown();

        /*

        Futures.addCallback(client.findConnection(), new FutureCallback<MultiplexProtocol>() {
            @Override
            public void onSuccess(@NullableDecl MultiplexProtocol multiplexProtocol) {
                System.err.println("found protocol");
                multiplexProtocol.connect("space", "123", "me", "life", new DefunctDocumentEventsFactory() {
                    @Override
                    public RemoteDocumentEvents make(MultiplexProtocol.DocumentConnection connection) {
                        System.err.println("connected");
                        return new RemoteDocumentEvents() {
                            @Override
                            public void delta(String data) {
                                System.err.println("DELTA:" + data);
                            }

                            @Override
                            public void connected() {
                                System.err.println("Connected");
                            }

                            @Override
                            public void disconnected() {
                                System.err.println("Disconnected");
                            }

                            @Override
                            public void error(int code) {
                                System.err.println("Error:" + code);
                            }
                        };
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }
        }, inMemoryThread);
        */
    }
}
