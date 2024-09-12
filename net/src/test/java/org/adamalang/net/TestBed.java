/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.net;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.net.client.TestClientConfig;
import org.adamalang.net.client.mocks.MockFinderService;
import org.adamalang.net.mocks.MockBackupService;
import org.adamalang.net.mocks.MockMetricsReporter;
import org.adamalang.net.mocks.MockReplicationInitiator;
import org.adamalang.runtime.data.BoundLocalFinderService;
import org.adamalang.runtime.deploy.*;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.net.client.contracts.RoutingTarget;
import org.adamalang.net.mocks.NaughyHandler;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.cron.NoOpWakeService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringBatchReady;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.junit.Assert;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBed implements AutoCloseable {
  public final NetBase base;
  public final int port;
  public final MachineIdentity identity;
  public final SimpleExecutor clientExecutor;
  public final MeteringPubSub meteringPubSub;
  public final ServerNexus nexus;
  public final AtomicInteger deploymentScans;
  public final CoreService coreService;
  private ServerHandle handle;
  private CountDownLatch serverExit;
  public final DiskMeteringBatchMaker batchMaker;
  private final File billingRoot;
  public final ClientConfig clientConfig;
  public final MockFinderService finderService;
  public final MockMetricsReporter metricsReporter;

  public TestBed(int port, String code) throws Exception {
    sanityCompileForTestbed(code);
    this.base = new NetBase(new NetMetrics(new NoOpMetricsFactory()), MachineIdentity.fromFile(prefixForLocalhost()), 1, 2);
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

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), Callback.FINISHED_LATCH_DONT_CARE_VOID(latch));
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));

    ExecutorService inMemoryThread = Executors.newSingleThreadScheduledExecutor();
    this.meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, base);
    this.metricsReporter = new MockMetricsReporter();
    this.coreService =
        new CoreService(
            new CoreMetrics(new NoOpMetricsFactory()),
            base, //
            meteringPubSub.publisher(), //
            metricsReporter, //
            new InMemoryDataService(inMemoryThread, TimeSource.REAL_TIME), //
            new MockBackupService(), //
            new NoOpWakeService(), //
            new MockReplicationInitiator("{\"x\":421369}", null), //
            TimeSource.REAL_TIME, //
            2);

    this.identity = this.base.identity;

    billingRoot = new File(File.createTempFile("ADAMATEST_",  "x23").getParentFile(), "Billing-" + System.currentTimeMillis());
    billingRoot.mkdir();

    this.batchMaker = new DiskMeteringBatchMaker(TimeSource.REAL_TIME, clientExecutor, billingRoot, 1800000L, new MeteringBatchReady() {
      @Override
      public void init(DiskMeteringBatchMaker me) {

      }

      @Override
      public void ready(String batchId) {
        System.out.println("Metering Batch Ready:" + batchId);
      }
    });
    finderService = new MockFinderService("the-machine");
    BoundLocalFinderService finder = new BoundLocalFinderService(this.clientExecutor, finderService, "the-region", "the-machine");
    this.nexus = new ServerNexus(this.base, identity, coreService, new ServerMetrics(new NoOpMetricsFactory()), base, finder, new Deploy() {
      @Override
      public void deploy(String space, Callback<Void> callback) {
        if (deploymentScans.incrementAndGet() == 3) {
          callback.failure(new ErrorCodeException(-13));
          return;
        }
        callback.success(null);
      }
    }, meteringPubSub, batchMaker, port, 2);
    this.handle = null;
    this.serverExit = null;
    this.clientConfig = new TestClientConfig();
  }

  public InstanceClient makeClient() throws Exception {
    return makeClient(null);
  }

  public InstanceClient makeClient(HeatMonitor monitor) throws Exception {
    LocalRegionClientMetrics metrics = new LocalRegionClientMetrics(new NoOpMetricsFactory());
    return new InstanceClient(base, clientConfig, metrics, monitor, new RoutingTarget() {
      @Override
      public void integrate(String target, Collection<String> spaces) {

      }
    },"127.0.0.1:" + port, clientExecutor);
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
    if (handle == null) {
      handle = this.base.serve(port, (upstream) -> new Handler(nexus, upstream));
      serverExit = new CountDownLatch(1);
      CountDownLatch waitUntilThreadUp = new CountDownLatch(1);
      new Thread(() -> {
        waitUntilThreadUp.countDown();
        handle.waitForEnd();
        serverExit.countDown();
      }).start();
      Assert.assertTrue(waitUntilThreadUp.await(1000, TimeUnit.MILLISECONDS));
    } else {
      Assert.fail();
    }
  }

  public void startManual(org.adamalang.common.net.Handler handler) throws Exception {
    if (handle == null) {
      handle = this.base.serve(port, handler);
      serverExit = new CountDownLatch(1);
      CountDownLatch waitUntilThreadUp = new CountDownLatch(1);
      new Thread(() -> {
        waitUntilThreadUp.countDown();
        handle.waitForEnd();
        serverExit.countDown();
      }).start();
      Assert.assertTrue(waitUntilThreadUp.await(1000, TimeUnit.MILLISECONDS));
    } else {
      Assert.fail();
    }
  }

  public NaughyHandler.NaughtyBits naughty() {
    return new NaughyHandler.NaughtyBits(this);
  }

  public void stopServer() throws Exception {
    if (handle != null) {
      handle.kill();
      Assert.assertTrue(serverExit.await(5000, TimeUnit.MILLISECONDS));
      handle = null;
      serverExit = null;
    } else {
      Assert.fail();
    }
  }

  @Override
  public void close() throws Exception {
    for (File file : billingRoot.listFiles()) {
      file.delete();
    }
    billingRoot.delete();
    if (handle != null) {
      stopServer();
    }
    base.shutdown();
    clientExecutor.shutdown();
  }

  public static void sanityCompileForTestbed(String code) throws Exception {
    ObjectNode planNode = Json.newJsonObject();
    planNode.putObject("versions").put("main", code);
    planNode.put("default", "main");
    DeploymentPlan plan = new DeploymentPlan(planNode.toString(), (c, t) -> {});
    MessageDigest digest = Hashing.md5();
    digest.update(code.getBytes(StandardCharsets.UTF_8));
    AtomicBoolean success = new AtomicBoolean(false);
    CountDownLatch latch = new CountDownLatch(1);
    AsyncCompiler.forge(RuntimeEnvironment.Tooling, "space", null, plan, Deliverer.FAILURE, new TreeMap<>(), AsyncByteCodeCache.DIRECT, new Callback<DeploymentFactory>() {
      @Override
      public void success(DeploymentFactory value) {
        success.set(true);
        latch.countDown();
        System.out.println("success testbed deploy");
      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.out.println("failed testbed deploy:" + ex.code);
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
  }
}
