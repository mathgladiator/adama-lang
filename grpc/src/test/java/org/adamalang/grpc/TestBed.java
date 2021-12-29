/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.TimeSource;
import org.adamalang.grpc.server.Server;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TestBed implements AutoCloseable {
  public final ScheduledExecutorService clientExecutor;
  public final MachineIdentity identity;
  private final Server server;

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
    planWriter.writeString("x");
    ;
    planWriter.endObject();
    DeploymentPlan plan = new DeploymentPlan(planWriter.toString(), (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.deploy("space", plan);

    ExecutorService inMemoryThread = Executors.newSingleThreadScheduledExecutor();

    CoreService service =
        new CoreService(
            base, //
            new InMemoryDataService(inMemoryThread, TimeSource.REAL_TIME), //
            TimeSource.REAL_TIME,
            2);

    this.identity = MachineIdentity.fromFile(prefixForLocalhost());
    this.server = new Server(identity, service, port);
  }

  private String prefixForLocalhost() {
    for (String search : new String[] {"./", "../", "./grpc/"}) {
      String candidate = search + "localhost.identity";
      File file = new File(candidate);
      if (file.exists()) {
        return candidate;
      }
    }
    throw new NullPointerException("could not find identity.localhost");
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

  public void startServer() throws Exception {
    server.start();
  }

  public void stopServer() throws Exception {
    server.close();
  }

  @Override
  public void close() throws Exception {
    server.close();
    clientExecutor.shutdown();
  }

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
}
