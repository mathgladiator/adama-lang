package org.adamalang.grpc;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.adamalang.grpc.client.Client;
import org.adamalang.grpc.client.DocumentEvents;
import org.adamalang.grpc.client.DocumentEventsFactory;
import org.adamalang.grpc.client.MultiplexProtocol;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.grpc.server.Server;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.exceptions.ErrorCodeException;
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

public class Play {

    public static class FiniteDocumentFactory implements LivingDocumentFactoryFactory {
        public final HashMap<String, LivingDocumentFactory> factories;

        public FiniteDocumentFactory() {
            this.factories = new HashMap<>();
        }

        @Override
        public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
            LivingDocumentFactory factory = factories.get(key.space);
            if (factory == null) {
                callback.failure(new ErrorCodeException(12345));
                return;
            }
            callback.success(factory);
        }
    }

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

    public static void main(String[] args) throws Exception {
        ExecutorService inMemoryThread = Executors.newSingleThreadScheduledExecutor();
        FiniteDocumentFactory factory = new FiniteDocumentFactory();
        factory.factories.put("space", compile("@connected(who) { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } "));

        CoreService service = new CoreService(factory, //
                new InMemoryDataService(inMemoryThread, TimeSource.REAL_TIME), //
                TimeSource.REAL_TIME, 2);

        MachineIdentity identity = MachineIdentity.fromFile("./grpc/localhost.identity");

        Server server = new Server(identity, service, 2321);
        server.start();
        Client client = new Client(identity, "127.0.0.1:2321", inMemoryThread);
        Futures.addCallback(client.create("me", "life", "space", "123", null, "{}"), new FutureCallback<Void>() {
            @Override
            public void onSuccess(@NullableDecl Void unused) {
                System.err.println("Success!");
                // TODO: connect to download
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.err.println("Failure");
            }
        }, inMemoryThread);

        Futures.addCallback(client.findConnection(), new FutureCallback<MultiplexProtocol>() {
            @Override
            public void onSuccess(@NullableDecl MultiplexProtocol multiplexProtocol) {
                System.err.println("found protocol");
                multiplexProtocol.connect("space", "123", "me", "life", new DocumentEventsFactory() {
                    @Override
                    public DocumentEvents make(MultiplexProtocol.DocumentConnection connection) {
                        System.err.println("connected");
                        return new DocumentEvents() {
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
