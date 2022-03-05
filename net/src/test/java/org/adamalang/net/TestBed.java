/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.net.client.contracts.RoutingTarget;
import org.adamalang.net.mocks.StdErrLogger;
import org.adamalang.net.server.Server;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBed implements AutoCloseable {
  public final NetBase base;
  public final int port;
  public final MachineIdentity identity;
  public final SimpleExecutor clientExecutor;
  public final MeteringPubSub meteringPubSub;
  private final Server server;
  public final AtomicInteger deploymentScans;
  public final CoreService coreService;

  public TestBed(int port, String code) throws Exception {
    this.base = new NetBase(MachineIdentity.fromFile(prefixForLocalhost()), 1, 2);
    this.port = port;
    clientExecutor = SimpleExecutor.create("client-executor");
    deploymentScans = new AtomicInteger(0);
    JsonStreamWriter planWriter = new JsonStreamWriter();
    planWriter.beginObject();
    planWriter.writeObjectFieldIntro("versions");
    planWriter.beginObject();
    planWriter.writeObjectFieldIntro("x");
    planWriter.writeString(code);
    planWriter.endObject();
    planWriter.writeObjectFieldIntro("default");
    planWriter.writeString("x");
    planWriter.endObject();
    DeploymentPlan plan = new DeploymentPlan(planWriter.toString(), (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.deploy("space", plan);

    ExecutorService inMemoryThread = Executors.newSingleThreadScheduledExecutor();
    this.meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, base);

    this.coreService =
        new CoreService(
            new CoreMetrics(new NoOpMetricsFactory()),
            base, //
            meteringPubSub.publisher(), //
            new InMemoryDataService(inMemoryThread, TimeSource.REAL_TIME), //
            TimeSource.REAL_TIME,
            2);

    this.identity = this.base.identity;
    ServerNexus nexus = new ServerNexus(this.base, identity, coreService, new ServerMetrics(new NoOpMetricsFactory()), base, (space) -> {
      if (deploymentScans.incrementAndGet() == 3) {
        throw new NullPointerException();
      }
    }, meteringPubSub, new DiskMeteringBatchMaker(TimeSource.REAL_TIME, clientExecutor, File.createTempFile("x23", "x23").getParentFile(),  1800000L), port, 2);
    this.server = new Server(nexus);
  }

  public InstanceClient makeClient() throws Exception {
    return makeClient(null);
  }

  public InstanceClient makeClient(HeatMonitor monitor) throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    return new InstanceClient(base, metrics, monitor, new RoutingTarget() {
      @Override
      public void integrate(String target, Collection<String> spaces) {

      }
    },"127.0.0.1:" + port, clientExecutor, new StdErrLogger());
  }

  public static String prefixForLocalhost() {
    for (String search : new String[] {"./", "../", "./grpc/"}) {
      String candidate = search + "localhost.identity";
      File file = new File(candidate);
      if (file.exists()) {
        return candidate;
      }
    }
    throw new NullPointerException("could not find identity.localhost");
  }

  public void startServer() throws Exception {
    server.start();
  }

  public void stopServer() throws Exception {
    base.shutdown();
  }

  @Override
  public void close() throws Exception {
    base.shutdown();
    clientExecutor.shutdown();
  }
}
