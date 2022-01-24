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
import org.adamalang.extern.Email;
import org.adamalang.extern.ExternNexus;
import org.adamalang.frontend.BootstrapFrontend;
import org.adamalang.grpc.client.Client;
import org.adamalang.grpc.client.ClientMetrics;
import org.adamalang.grpc.server.Server;
import org.adamalang.grpc.server.ServerMetrics;
import org.adamalang.grpc.server.ServerNexus;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.backend.BackendDataServiceInstaller;
import org.adamalang.mysql.backend.BlockingDataService;
import org.adamalang.mysql.deployments.DeployedInstaller;
import org.adamalang.mysql.deployments.Deployments;
import org.adamalang.mysql.frontend.FrontendManagementInstaller;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;
import org.junit.Assert;

import java.io.File;
import java.nio.file.Files;
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
  public final SimpleExecutor clientExecutor;
  public final DeploymentFactoryBase deploymentFactoryBase;
  public final CoreService coreService;
  public final Server server;

  public TestFrontEnd() throws Exception {
    int port = 10000;
    codesSentToEmail = new ConcurrentHashMap<>();
    String config = Files.readString(new File("./test.mysql.json").toPath());
    DataBase dataBase = new DataBase(new DataBaseConfig(new ConfigObject(Json.parseJsonObject(config)), "any"));
    this.installerFront = new FrontendManagementInstaller(dataBase);
    installerFront.install();
    this.installerBack = new BackendDataServiceInstaller(dataBase);
    installerBack.install();
    this.installDeploy = new DeployedInstaller(dataBase);
    BlockingDataService ds = new BlockingDataService(dataBase);
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
          Deployments.Deployment deployment = Deployments.get(dataBase, identity.ip + ":" + port, space);
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
    this.nexus = new ExternNexus(this, dataBase, dataBase, dataBase, client, new NoOpMetricsFactory());
    this.frontend = BootstrapFrontend.make(nexus);
    this.context = new ConnectionContext("home", "ip", "agent");
    connection = this.frontend.establish(context);
    emailLatch = new ConcurrentHashMap<>();
  }

  @Override
  public void close() throws Exception {
    installerFront.uninstall();
    installerBack.uninstall();
    nexus.close();
    clientExecutor.shutdown();
    server.close();
  }

  @Override
  public void sendCode(String email, String code) {
    codesSentToEmail.put(email, code);
    CountDownLatch latch = emailLatch.remove(email);
    if (latch != null) {
      latch.countDown();
    }
  }

  public String generateIdentity(String email) {
    Runnable latch1 = latchOnEmail(email);
    Iterator<String> c1 =
        execute("{\"id\":1,\"method\":\"init/start\",\"email\":\"" + email + "\"}");
    latch1.run();
    Iterator<String> c2 =
        execute(
            "{\"id\":2,\"connection\":1,\"method\":\"init/generate-identity\",\"code\":\""
                + codesSentToEmail.remove(email)
                + "\"}");
    String result1 = c2.next();
    Assert.assertTrue(result1.length() > 0);
    Assert.assertEquals("FINISH:{\"identity\":", result1.substring(0, 19));
    String identity1 = Json.parseJsonObject(result1.substring(7)).get("identity").textValue();
    Assert.assertEquals("FINISH:{}", c1.next());
    return identity1;
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
    JsonRequest request = new JsonRequest(Json.parseJsonObject(requestJson));
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
}
