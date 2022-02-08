/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.extern.AssetUploader;
import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.frontend.BootstrapFrontend;
import org.adamalang.frontend.FrontendConfig;
import org.adamalang.grpc.client.Client;
import org.adamalang.grpc.client.ClientMetrics;
import org.adamalang.grpc.server.Server;
import org.adamalang.grpc.server.ServerMetrics;
import org.adamalang.grpc.server.ServerNexus;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.backend.BackendDataServiceInstaller;
import org.adamalang.mysql.backend.BackendMetrics;
import org.adamalang.mysql.backend.BlockingDataService;
import org.adamalang.mysql.deployments.DeployedInstaller;
import org.adamalang.mysql.deployments.Deployments;
import org.adamalang.mysql.deployments.data.Deployment;
import org.adamalang.mysql.frontend.FrontendManagementInstaller;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonLogger;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.junit.Assert;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestFrontEnd implements AutoCloseable, Email {

  public final ConcurrentHashMap<String, CountDownLatch> emailLatch;
  public final ConcurrentHashMap<String, String> codesSentToEmail;
  public final ExternNexus nexus;
  public final ServiceBase frontend;
  public final ConnectionContext context;
  public final ServiceConnection connection;
  public final FrontendManagementInstaller installerFront;
  public final BackendDataServiceInstaller installerBack;
  public final DeployedInstaller installDeploy;
  public final File attachmentRoot;
  public final SimpleExecutor clientExecutor;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final CoreService coreService;
  public final Server server;

  public TestFrontEnd() throws Exception {
    int port = 10000;
    codesSentToEmail = new ConcurrentHashMap<>();
    String config = Files.readString(new File("./test.mysql.json").toPath());
    DataBase dataBase = new DataBase(new DataBaseConfig(new ConfigObject(Json.parseJsonObject(config)), "any"), new DataBaseMetrics(new NoOpMetricsFactory(), "noop"));
    this.installerFront = new FrontendManagementInstaller(dataBase);
    this.installerFront.install();
    this.installerBack = new BackendDataServiceInstaller(dataBase);
    this.installerBack.install();
    this.installDeploy = new DeployedInstaller(dataBase);
    this.installDeploy.install();
    BlockingDataService ds = new BlockingDataService(new BackendMetrics(new NoOpMetricsFactory()), dataBase);
    deploymentFactoryBase = new DeploymentFactoryBase();
    MeteringPubSub meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, deploymentFactoryBase);
    coreService =
        new CoreService(
            new CoreMetrics(new NoOpMetricsFactory()),
            deploymentFactoryBase, //
            meteringPubSub.publisher(), //
            ds, //
            TimeSource.REAL_TIME,
            1);

    // TODO: setup a backend server
    MachineIdentity identity = MachineIdentity.fromFile("localhost.identity");

    this.clientExecutor = SimpleExecutor.create("disk");
    ServerNexus backendNexus = new ServerNexus(identity, coreService, new ServerMetrics(new NoOpMetricsFactory()), deploymentFactoryBase, (space) -> {
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
    }, meteringPubSub, new DiskMeteringBatchMaker(TimeSource.REAL_TIME, clientExecutor, File.createTempFile("x23", "x23").getParentFile(),  1800000L), port, 2);

    server = new Server(backendNexus);
    server.start();
    Client client = new Client(identity, new ClientMetrics(new NoOpMetricsFactory()), null);
    client.getTargetPublisher().accept(Collections.singletonList("127.0.0.1:" + port));
    this.attachmentRoot = new File(File.createTempFile("x23", "x23").getParentFile(), "inflight." + System.currentTimeMillis());
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
    FrontendConfig frontendConfig = new FrontendConfig(new ConfigObject(Json.parseJsonObject("{\"threads\":2}")));
    this.nexus = new ExternNexus(frontendConfig, this, uploader, dataBase, dataBase, dataBase, client, new NoOpMetricsFactory(), attachmentRoot, JsonLogger.NoOp);
    this.frontend = BootstrapFrontend.make(nexus, HttpHandler.NULL);
    this.context = new ConnectionContext("home", "ip", "agent");
    connection = this.frontend.establish(context);
    emailLatch = new ConcurrentHashMap<>();
  }

  public void kill(String table) throws Exception {
    try (Connection connection = nexus.dataBaseManagement.pool.getConnection()) {
      DataBase.execute(connection, new StringBuilder("DROP TABLE IF EXISTS `").append(nexus.dataBaseManagement.databaseName).append("`.`").append(table).append("`;").toString());
    }
  }

  @Override
  public void close() throws Exception {
    installerFront.uninstall();
    installerBack.uninstall();
    installDeploy.uninstall();
    nexus.close();
    clientExecutor.shutdown();
    server.close();
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
    JsonRequest request = new JsonRequest(Json.parseJsonObject(requestJson), new ConnectionContext("ip", "origin", "agent"));
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
