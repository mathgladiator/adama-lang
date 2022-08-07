/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.caravan.CaravanDataService;
import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.caravan.data.DurableListStore;
import org.adamalang.caravan.data.DiskMetrics;
import org.adamalang.caravan.events.FinderServiceToKeyToIdService;
import org.adamalang.common.*;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.extern.AssetUploader;
import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.frontend.BootstrapFrontend;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.Installer;
import org.adamalang.mysql.model.Deployments;
import org.adamalang.mysql.data.Deployment;
import org.adamalang.mysql.model.Finder;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.ManagedDataService;
import org.adamalang.runtime.data.managed.Base;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.contracts.AssetDownloader;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonLogger;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.adamalang.web.service.AssetRequest;
import org.adamalang.web.service.WebConfig;
import org.junit.Assert;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyPair;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestFrontEnd implements AutoCloseable, Email {

  public final ConcurrentHashMap<String, CountDownLatch> emailLatch;
  public final ConcurrentHashMap<String, String> codesSentToEmail;
  public final ExternNexus nexus;
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

  public final File caravanPath;
  public TestFrontEnd() throws Exception {
    int port = 10000;
    codesSentToEmail = new ConcurrentHashMap<>();
    String config = Files.readString(new File("./test.mysql.json").toPath());
    DataBase dataBase = new DataBase(new DataBaseConfig(new ConfigObject(Json.parseJsonObject(config))), new DataBaseMetrics(new NoOpMetricsFactory()));
    this.installer = new Installer(dataBase);
    this.installer.install();
    this.alive = new AtomicBoolean(true);
    MachineIdentity identity = MachineIdentity.fromFile("localhost.identity");
    Spaces.createSpace(dataBase, 0, "ide");
    {
      ObjectNode plan = Json.newJsonObject();
      plan.putObject("versions").put("file", "@static { create { return true; } }");
      plan.put("default", "file");
      plan.putArray("plan");
      String planJson = plan.toString();
      Deployments.deploy(dataBase, "ide", identity.ip + ":" + port, "hash", planJson);
    }
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
    Finder finder = new Finder(dataBase, "test-region");
    Cloud cloud = new Cloud() {
      @Override
      public File path() {
        return cloudPath;
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
      public void delete(Key key, String archiveKey) {
      }
    };
    CaravanDataService caravanDataService = new CaravanDataService(cloud, new FinderServiceToKeyToIdService(finder), store, caravanExecutor);
    Base managedBase = new Base(finder, caravanDataService, "test-region", identity.ip + ":" + port, managedExecutor, 5 * 60 * 1000);
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
    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    coreService =
        new CoreService(
            new CoreMetrics(new NoOpMetricsFactory()),
            deploymentFactoryBase, //
            meteringPubSub.publisher(), //
            dataService, //
            TimeSource.REAL_TIME,
            1);

    this.netBase = new NetBase(new NetMetrics(new NoOpMetricsFactory()), identity, 1, 2);
    this.clientExecutor = SimpleExecutor.create("disk");
    ServerNexus backendNexus = new ServerNexus(netBase, identity, coreService, new ServerMetrics(new NoOpMetricsFactory()), deploymentFactoryBase, (space) -> {
      try {
        if (!"*".equals(space)) {
          Deployment deployment = Deployments.get(dataBase, identity.ip + ":" + port, space);
          deploymentFactoryBase.deploy(deployment.space, new DeploymentPlan(deployment.plan, (x, y) -> {
          }));
          coreService.deploy(new DeploymentMonitor() {
            @Override
            public void bumpDocument(boolean changed) {

            }

            @Override
            public void witnessException(ErrorCodeException ex) {

            }
          });
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }, meteringPubSub, new DiskMeteringBatchMaker(TimeSource.REAL_TIME, clientExecutor, File.createTempFile("ADAMATEST_", "x23").getParentFile(),  1800000L), port, 2);
    backendNexus.scanForDeployments.accept("ide");
    serverHandle = netBase.serve(port, (upstream -> new Handler(backendNexus, upstream)));
    ClientConfig clientConfig = new ClientConfig();
    ClientMetrics clientMetrics =  new ClientMetrics(new NoOpMetricsFactory());
    ClientRouter router = ClientRouter.FINDER(clientMetrics, finder, "test-region");
    Client client = new Client(netBase, clientConfig, clientMetrics, router, null);
    client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:" + port));

    CountDownLatch waitForRouting = new CountDownLatch(1);
    do {
      System.err.println("Waiting for routing table to be built...");
      router.engine.get(new Key("ide", "default"), new RoutingSubscriber() {
        @Override
        public void onRegion(String region) {
        }

        @Override
        public void onMachine(String machine) {
          if (machine != null) {
            waitForRouting.countDown();
          } else {
            System.err.println("found null");
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
    } while (!waitForRouting.await(250, TimeUnit.MILLISECONDS));

    this.attachmentRoot = new File(File.createTempFile("ADAMATEST_", "x23").getParentFile(), "inflight." + System.currentTimeMillis());
    AssetUploader uploader = new AssetUploader() {
      @Override
      public void upload(Key key, NtAsset asset, File localFile, Callback<Void> callback) {
        try {
          Files.copy(localFile.toPath(), new File(localFile.getParent(), localFile.getName() + ".done").toPath());
          callback.success(null);
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(-1, ex));
        }
      }
    };
    AssetDownloader downloader = new AssetDownloader() {
      @Override
      public void request(AssetRequest request, AssetStream stream) {

      }
    };
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(Json.parseJsonObject("{\"threads\":2}")));
    this.webBase = new WebClientBase(new WebConfig(new ConfigObject(Json.parseJsonObject("{}"))));
    this.hostKeyPair = Keys.keyPairFor(SignatureAlgorithm.ES256);
    int keyId = Hosts.initializeHost(dataBase, "region", "127.0.0.1:" + port, "web", PerSessionAuthenticator.encodePublicKey(hostKeyPair));
    this.nexus = new ExternNexus(frontendConfig, this, uploader, downloader, dataBase, finder, client, new NoOpMetricsFactory(), attachmentRoot, JsonLogger.NoOp, MasterKey.generateMasterKey(), webBase, "region", hostKeyPair.getPrivate(), keyId);

    this.frontend = BootstrapFrontend.make(nexus, HttpHandler.NULL);
    this.context = new ConnectionContext("home", "ip", "agent", null);
    connection = this.frontend.establish(context);
    frontend.http();
    emailLatch = new ConcurrentHashMap<>();
  }

  public void kill(String table) throws Exception {
    try (Connection connection = nexus.dataBase.pool.getConnection()) {
      DataBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(nexus.dataBase.databaseName).append("`.`").append(table).append("`;").toString());
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
    JsonRequest request = new JsonRequest(Json.parseJsonObject(requestJson), new ConnectionContext("ip", "origin", "agent", null));
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
