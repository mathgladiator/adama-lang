/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.auth.GlobalAuthenticator;
import org.adamalang.caravan.CaravanDataService;
import org.adamalang.caravan.CaravanMetrics;
import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.common.*;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.PrivateKeyWithId;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.extern.MockMetricsReporter;
import org.adamalang.extern.MockPostDocumentDelete;
import org.adamalang.extern.SignalControl;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.impl.GlobalCapacityOverseer;
import org.adamalang.mysql.impl.GlobalFinder;
import org.adamalang.mysql.impl.GlobalPlanFetcher;
import org.adamalang.mysql.model.*;
import org.adamalang.runtime.data.BoundLocalFinderService;
import org.adamalang.runtime.deploy.DeploySync;
import org.adamalang.runtime.deploy.DeploymentMetrics;
import org.adamalang.runtime.deploy.OndemandDeploymentFactoryBase;
import org.adamalang.runtime.sys.capacity.CapacityAgent;
import org.adamalang.runtime.sys.capacity.CapacityMetrics;
import org.adamalang.runtime.sys.ServiceHeatEstimator;
import org.adamalang.runtime.sys.metering.MeteringBatchReady;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.contracts.*;
import org.adamalang.extern.Email;
import org.adamalang.frontend.global.GlobalExternNexus;
import org.adamalang.frontend.global.BootstrapGlobalServiceBase;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.Installer;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.ManagedDataService;
import org.adamalang.runtime.data.managed.Base;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.ServiceShield;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonLogger;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.service.WebConfig;
import org.junit.Assert;
import org.junit.Assume;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyPair;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestFrontEnd implements AutoCloseable, Email {
  public final ConcurrentHashMap<String, CountDownLatch> emailLatch;
  public final ConcurrentHashMap<String, String> codesSentToEmail;
  public final GlobalExternNexus nexus;
  public final ServiceBase frontend;
  public final ConnectionContext context;
  public final ServiceConnection connection;
  public final Installer installer;
  public final File attachmentRoot;
  public final SimpleExecutor clientExecutor;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final CoreService coreService;
  public final NetBase netBase;
  public final ServerHandle serverHandle;
  public final AtomicBoolean alive;
  public final DurableListStore store;
  private final CountDownLatch threadDeath;
  private final WebClientBase webBase;
  private final KeyPair hostKeyPair;

  public final CapacityAgent capacityAgent;
  public final File caravanPath;
  public final MockPostDocumentDelete delete;

  private static String getIDE() throws Exception {
    File file = new File("../internal/ide.adama");
    if (!file.exists()) {
      file = new File("internal/ide.adama");
    }
    return Files.readString(file.toPath());
  }

  private static String getBilling() throws Exception {
    File file = new File("../internal/billing.adama");
    if (!file.exists()) {
      file = new File("internal/billing.adama");
    }
    return Files.readString(file.toPath());
  }

  public TestFrontEnd() throws Exception {
    int port = 10000;
    codesSentToEmail = new ConcurrentHashMap<>();
    File configFile = new File("./test.mysql.json");
    Assume.assumeTrue(configFile.exists());
    String config = Files.readString(configFile.toPath());
    DataBase dataBase = new DataBase(new DataBaseConfig(new ConfigObject(Json.parseJsonObject(config))), new DataBaseMetrics(new NoOpMetricsFactory()));
    this.installer = new Installer(dataBase);
    this.installer.install();

    this.alive = new AtomicBoolean(true);
    MachineIdentity identity = MachineIdentity.fromFile("localhost.identity");
    int ideId = Spaces.createSpace(dataBase, 0, "ide");
    {
      ObjectNode plan = Json.newJsonObject();
      plan.putObject("versions").put("file", getIDE());
      plan.put("default", "file");
      plan.putArray("plan");
      String planJson = plan.toString();
      Spaces.setPlan(dataBase, ideId, planJson, "hash");
    }
    int billingId = Spaces.createSpace(dataBase, 0, "billing");
    {
      ObjectNode plan = Json.newJsonObject();
      plan.putObject("versions").put("file", getBilling());
      plan.put("default", "file");
      plan.putArray("plan");
      String planJson = plan.toString();
      Spaces.setPlan(dataBase, billingId, planJson, "hash");
    }
    String masterKey = MasterKey.generateMasterKey();
    this.caravanPath = File.createTempFile("ADAMATEST_", "caravan");
    caravanPath.delete();
    caravanPath.mkdirs();
    SimpleExecutor caravanExecutor = SimpleExecutor.create("caravan");
    SimpleExecutor managedExecutor = SimpleExecutor.create("managed-base");
    caravanPath.mkdir();
    File walRoot = new File(caravanPath, "wal");
    File dataRoot = new File(caravanPath, "data");
    walRoot.mkdir();
    dataRoot.mkdir();
    File cloudPath = new File(caravanPath, "archive");
    File storePath = new File(dataRoot, "store");
    this.store = new DurableListStore(new DiskMetrics(new NoOpMetricsFactory()), storePath, walRoot, 64 * 1024 * 1024, 64 * 1024, 1024 * 1024);
    GlobalFinder globalFinder = new GlobalFinder(dataBase, "test-region", "127.0.0.1:" + port);
    Cloud cloud = new Cloud() {
      @Override
      public File path() {
        return cloudPath;
      }

      @Override
      public void exists(Key key, String archiveKey, Callback<Void> callback) {
        callback.failure(new ErrorCodeException(-13));
      }

      @Override
      public void restore(Key key, String archiveKey, Callback<File> callback) {
        callback.failure(new ErrorCodeException(-42));
      }

      @Override
      public void backup(Key key, File archiveFile, Callback<Void> callback) {
        callback.success(null);
      }

      @Override
      public void delete(Key key, String archiveKey, Callback<Void> callback) {
      }
    };
    CaravanDataService caravanDataService = new CaravanDataService(new CaravanMetrics(new NoOpMetricsFactory()), cloud, store, caravanExecutor);
    delete = new MockPostDocumentDelete();
    Base managedBase = new Base(globalFinder, caravanDataService, delete, "test-region", "127.0.0.1:" + port, managedExecutor, 5 * 60 * 1000);
    ManagedDataService dataService = new ManagedDataService(managedBase);
    threadDeath = new CountDownLatch(1);
    Thread flusher = new Thread(new Runnable() {
      @Override
      public void run() {
        while (alive.get()) {
          try {
            Thread.sleep(0, 800000);
            caravanDataService.flush(false).await(1000, TimeUnit.MILLISECONDS);
          } catch (InterruptedException ie) {
            return;
          }
        }
        threadDeath.countDown();
      }
    });
    flusher.start();

    deploymentFactoryBase = new DeploymentFactoryBase();
    // TODO: test the
    OndemandDeploymentFactoryBase ondemand = new OndemandDeploymentFactoryBase(new DeploymentMetrics(new NoOpMetricsFactory()), deploymentFactoryBase, new GlobalPlanFetcher(dataBase, masterKey), new DeploySync() {
      @Override
      public void watch(String space) {

      }

      @Override
      public void unwatch(String space) {

      }
    });

    MockMetricsReporter metricsReporter = new MockMetricsReporter();
    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    coreService =
        new CoreService(
            new CoreMetrics(new NoOpMetricsFactory()),
            ondemand, //
            meteringPubSub.publisher(), //
            metricsReporter,
            dataService, //
            TimeSource.REAL_TIME,
            1);
    this.netBase = new NetBase(new NetMetrics(new NoOpMetricsFactory()), identity, 1, 2);
    this.clientExecutor = SimpleExecutor.create("disk");
    BoundLocalFinderService finder = new BoundLocalFinderService(this.clientExecutor, globalFinder, "test-region", identity.ip + ":" + port);
    ServerNexus backendNexus = new ServerNexus(netBase, identity, coreService, new ServerMetrics(new NoOpMetricsFactory()), deploymentFactoryBase, finder, ondemand, meteringPubSub, new DiskMeteringBatchMaker(TimeSource.REAL_TIME, clientExecutor, File.createTempFile("ADAMATEST_", "x23").getParentFile(), 1800000L, new MeteringBatchReady() {
      @Override
      public void init(DiskMeteringBatchMaker me) {

      }

      @Override
      public void ready(String batchId) {
        System.out.println("Metering batch ready:" + batchId);
      }
    }), port, 2);
    serverHandle = netBase.serve(port, (upstream -> new Handler(backendNexus, upstream)));
    ClientConfig clientConfig = new ClientConfig();
    LocalRegionClientMetrics localRegionClientMetrics =  new LocalRegionClientMetrics(new NoOpMetricsFactory());
    LocalRegionClient client = new LocalRegionClient(netBase, clientConfig, localRegionClientMetrics, null);
    client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:" + port));

    ServiceHeatEstimator.HeatVector low = new ServiceHeatEstimator.HeatVector(10000, 100, 1000*1000, 100);
    ServiceHeatEstimator.HeatVector hot = new ServiceHeatEstimator.HeatVector(1000L * 1000L * 1000L, 100000, 1000*1000*500L, 250L);
    ServiceHeatEstimator estimator = new ServiceHeatEstimator(low, hot);
    capacityAgent = new CapacityAgent(new CapacityMetrics(new NoOpMetricsFactory()), new GlobalCapacityOverseer(dataBase), coreService, deploymentFactoryBase, estimator, caravanExecutor, alive, new ServiceShield(), "test-region", identity.ip + ":" + port);

    // new fast path for routing table
    CountDownLatch waitForRouting = new CountDownLatch(1);
    client.getMachineFor(new Key("ide", "default"), new Callback<String>() {
      @Override
      public void success(String machine) {
        waitForRouting.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    backendNexus.deployer.deploy("ide", Callback.DONT_CARE_VOID);
    do {
      System.err.println("Waiting for routing table to be built...");
      client.getMachineFor(new Key("ide", "default"), new Callback<String>() {
        @Override
        public void success(String machine) {
          waitForRouting.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
    } while (!waitForRouting.await(250, TimeUnit.MILLISECONDS));

    this.attachmentRoot = new File(File.createTempFile("ADAMATEST_", "x23").getParentFile(), "inflight." + System.currentTimeMillis());
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(Json.parseJsonObject("{\"threads\":2}")));

    this.webBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(Json.parseJsonObject("{}"))));
    this.hostKeyPair = Jwts.SIG.ES256.keyPair().build();
    int keyId = Hosts.initializeHost(dataBase, "test-region", "127.0.0.1:" + port, "web", PublicKeyCodec.encodePublicKey(hostKeyPair));

    MultiRegionClient adama = new MultiRegionClient(client, "test-region", hostKeyPair.getPrivate(), keyId, new TreeMap<>());
    AssetSystem assets = new AssetSystem() {
      @Override
      public void request(AssetRequest request, AssetStream stream) {
        stream.failure(-12);
      }

      @Override
      public void request(Key key, NtAsset asset, AssetStream stream) {
        stream.failure(-15);
      }

      @Override
      public void attach(String identity, ConnectionContext context, Key key, NtAsset asset, String channel, String message, Callback<Integer> callback) {
        callback.failure(new ErrorCodeException(-13));
      }

      @Override
      public void upload(Key key, NtAsset asset, AssetUploadBody body, Callback<Void> callback) {
        try {
          File localFile = body.getFileIfExists();
          if (localFile != null) {
            Files.copy(localFile.toPath(), new File(localFile.getParent(), localFile.getName() + ".done").toPath());
            callback.success(null);
          } else {
            callback.failure(new ErrorCodeException(-2));
          }
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(-1, ex));
        }
      }
    };
    SignalControl signalControl = new SignalControl() {
      @Override
      public void raiseAutomaticDomain(String domain) {
        System.err.println("domain needs certificate:" + domain);
      }
    };
    GlobalAuthenticator authenticator = new GlobalAuthenticator(dataBase, clientExecutor);
    this.nexus = new GlobalExternNexus(frontendConfig, this, dataBase, adama, authenticator, assets, new NoOpMetricsFactory(), attachmentRoot, JsonLogger.NoOp, masterKey, webBase, "test-region", "test-machine", hostKeyPair.getPrivate(), keyId, new String[] {}, new String[] {}, signalControl, globalFinder, new PrivateKeyWithId(0, hostKeyPair.getPrivate()));
    this.frontend = BootstrapGlobalServiceBase.make(nexus, HttpHandler.NULL);
    this.context = new ConnectionContext("home", "ip", "agent", null, null);
    connection = this.frontend.establish(context);
    frontend.http();
    emailLatch = new ConcurrentHashMap<>();
  }

  public void kill(String table) throws Exception {
    try (Connection connection = nexus.database.pool.getConnection()) {
      DataBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(nexus.database.databaseName).append("`.`").append(table).append("`;").toString());
    }
  }

  @Override
  public void close() throws Exception {
    alive.set(false);
    threadDeath.await(5000, TimeUnit.MILLISECONDS);
    installer.uninstall();
    connection.kill();
    nexus.close();
    clientExecutor.shutdown();
    serverHandle.kill();
    netBase.shutdown();
    serverHandle.waitForEnd();
    connection.keepalive();
  }

  @Override
  public boolean sendCode(String email, String code) {
    codesSentToEmail.put(email, code);
    CountDownLatch latch = emailLatch.remove(email);
    if (latch != null) {
      latch.countDown();
    }
    return true;
  }

  @Override
  public boolean sendWelcome(String email) {
    return true;
  }

  public Runnable latchOnEmail(String email) {
    CountDownLatch latch = new CountDownLatch(1);
    emailLatch.put(email, latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      } catch (InterruptedException ie) {
        Assert.fail();
      }
    };
  }

  public Iterator<String> execute(String requestJson) {
    System.err.println("EXECUTE:" + requestJson);
    JsonRequest request = new JsonRequest(Json.parseJsonObject(requestJson), new ConnectionContext("ip", "origin", "agent", null, null));
    SyncIterator iterator = new SyncIterator();
    connection.execute(request, iterator);
    return iterator;
  }

  public static class SyncIterator implements Iterator<String>, JsonResponder {
    private final ArrayList<String> d;
    private CountDownLatch latch;

    public SyncIterator() {
      this.d = new ArrayList<>();
      this.latch = null;
    }

    @Override
    public boolean hasNext() {
      return true;
    }

    @Override
    public String next() {
      Object optimistic = get();
      if (optimistic instanceof String) {
        return (String) optimistic;
      } else {
        try {
          if (((CountDownLatch) optimistic).await(10000, TimeUnit.MILLISECONDS)) {
            return (String) get();
          }
          throw new Exception("timed out");
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
    }

    private synchronized Object get() {
      if (d.size() > 0) {
        latch = null;
        return d.remove(0);
      }
      latch = new CountDownLatch(1);
      return latch;
    }

    @Override
    public void stream(String json) {
      write("STREAM:" + json);
    }

    private synchronized void write(String data) {
      System.err.println("WRITING:" + data);
      d.add(data);
      if (latch != null) {
        latch.countDown();
      }
    }

    @Override
    public void finish(String json) {
      write("FINISH:" + json);
    }

    @Override
    public void error(ErrorCodeException ex) {
      write("ERROR:" + ex.code);
    }
  }

  public String setupDevIdentity() {
    return generateIdentity("x@x.com", false);
  }

  public String generateIdentity(String email, boolean revoke) {
    Runnable latch1 = latchOnEmail(email);
    Iterator<String> c1 = execute("{\"id\":1,\"method\":\"init/setup-account\",\"email\":\""+email+"\"}");
    latch1.run();
    Assert.assertEquals("FINISH:{}", c1.next());
    Iterator<String> c2 = execute("{\"id\":2,\"method\":\"init/complete-account\",\"email\":\""+email+"\",\"code\":\"" + codesSentToEmail.get(email) + "\""+(revoke ? ",\"revoke\":true" : "")+"}");
    String result1 = c2.next();
    Assert.assertTrue(result1.length() > 0);
    Assert.assertEquals("FINISH:{\"identity\":", result1.substring(0, 19));
    return Json.parseJsonObject(result1.substring(7)).get("identity").textValue();
  }
}
