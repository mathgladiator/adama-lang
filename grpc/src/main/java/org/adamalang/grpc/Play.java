package org.adamalang.grpc;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.adamalang.grpc.client.Client;
import org.adamalang.grpc.client.RemoteDocumentEvents;
import org.adamalang.grpc.client.DefunctDocumentEventsFactory;
import org.adamalang.grpc.client.MultiplexProtocol;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.grpc.server.Server;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Play {
    /*
    public static LivingDocumentFactory compile(final String code) throws Exception {
        final var options = CompilerOptions.start().enableCodeCoverage().noCost().make();
        final var globals = GlobalObjectPool.createPoolWithStdLib();
        final var state = new EnvironmentState(globals, options);
        final var document = new Document();
        document.setClassName("MeCode");
        final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
        final var parser = new Parser(tokenEngine);
        parser.document().accept(document);
        if (!document.check(state)) {
            throw new Exception("Failed to check:" + document.errorsJson());
        }
        final var java = document.compileJava(state);
        return new LivingDocumentFactory("MeCode", java, "{}");
    }
    */

    public static void main(String[] args) throws Exception {
        DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; }\"},\"default\":\"x\"}", (t, errorCode) -> {

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

        Client client = new Client(identity, "127.0.0.1:2321", inMemoryThread);
        System.err.println("Ping now!");
        client.ping(1000);
        System.err.println("Ping!");

        Futures.addCallback(client.create("me", "life", "space", "123", null, "{}"), new FutureCallback<Void>() {
            @Override
            public void onSuccess(@NullableDecl Void unused) {
                System.err.println("Success!");
                // TODO: connect to download
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.err.println("Failure");
                throwable.printStackTrace();
            }
        }, inMemoryThread);

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
    }
}
