package org.adamalang.grpc;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.adamalang.grpc.common.MachineIdentity;
import org.adamalang.grpc.server.Server;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.junit.Assert;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestBed implements AutoCloseable {
    public final ScheduledExecutorService clientExecutor;
    private final Server server;
    public final MachineIdentity identity;

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

    public TestBed(int port, String code) throws Exception {
        clientExecutor = Executors.newSingleThreadScheduledExecutor();
        JsonStreamWriter planWriter = new JsonStreamWriter();
        planWriter.beginObject();
        planWriter.writeObjectFieldIntro("versions");
        planWriter.beginObject();
        planWriter.writeObjectFieldIntro("x");
        planWriter.writeString(code);
        planWriter.endObject();
        planWriter.writeObjectFieldIntro("default");
        planWriter.writeString("x");;
        planWriter.endObject();
        DeploymentPlan plan = new DeploymentPlan(planWriter.toString(), (t, errorCode) -> {

        });
        DeploymentFactoryBase base = new DeploymentFactoryBase();
        base.deploy("space", plan);

        ExecutorService inMemoryThread = Executors.newSingleThreadScheduledExecutor();

        CoreService service = new CoreService(base, //
                new InMemoryDataService(inMemoryThread, TimeSource.REAL_TIME), //
                TimeSource.REAL_TIME, 2);

        this.identity = MachineIdentity.fromFile("localhost.identity");
        this.server = new Server(identity, service, port);

    }

    public <V> V assertGood(ListenableFuture<V> future) throws Exception {
        return future.get(5000, TimeUnit.MILLISECONDS);
    }

    public <V> void assertBad(ListenableFuture<V> future) throws Exception {
        boolean failure = true;
        try {
            future.get(5000, TimeUnit.MILLISECONDS);
            failure = false;
        } catch (Exception ex) {
        }
        Assert.assertTrue(failure);
    }


    public void startServer() throws Exception {
        server.start();
    }

    public void stopServer() throws Exception {
        server.stop();
    }

    @Override
    public void close() throws Exception {
        server.stop();
        clientExecutor.shutdown();
    }
}
