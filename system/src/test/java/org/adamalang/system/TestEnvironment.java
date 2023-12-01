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
package org.adamalang.system;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.api.ClientProbeRequest;
import org.adamalang.api.ClientSimpleResponse;
import org.adamalang.api.SelfClient;
import org.adamalang.common.*;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.Installer;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.mysql.model.Users;
import org.adamalang.system.contracts.JsonConfig;
import org.adamalang.system.distributed.Backend;
import org.adamalang.system.distributed.Frontend;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.client.socket.ConnectionReady;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolConfig;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolMetrics;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestEnvironment {
  public static TestEnvironment ENV;
  private int port;
  private final String masterKey;
  private final String superPublicKey;
  private final String superIdentity;
  private final String regionalPublicKey;
  private final String regionalPrivateKey;
  private final MetricsFactory metricsFactory;

  private final JsonConfig frontendGlobalConfig;
  private final JsonConfig[] backendGlobalConfigs;
  public final DataBase db;

  private Backend[] globalBackends;
  private Frontend globalFrontend;

  private final SimpleExecutor clients;
  private final WebClientBase clientBase;
  private final MultiWebClientRetryPool globalClientPool;

  public final SelfClient globalClient;

  public TestEnvironment() throws Exception {
    System.out.println("[test environment started]");
    scrub();
    this.masterKey = MasterKey.generateMasterKey();
    this.port = 25000;
    // connect to database for global region
    {
      KeyPair pair = Jwts.SIG.ES256.keyPair().build();
      superPublicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
      superIdentity = Jwts.builder().subject("super").issuer("super").signWith(pair.getPrivate()).compact();
    }
    {
      KeyPair pair = Jwts.SIG.ES256.keyPair().build();
      regionalPublicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
      regionalPrivateKey = new String(Base64.getEncoder().encode(pair.getPrivate().getEncoded()));
    }

    frontendGlobalConfig = assembleConfig(Role.Web, "central");
    int globalWebPort = frontendGlobalConfig.get_or_create_child("web").get("http-port").intValue();

    metricsFactory = new NoOpMetricsFactory();

    db = new DataBase(new DataBaseConfig(new ConfigObject(frontendGlobalConfig.read())), new DataBaseMetrics(metricsFactory));

    try {
      new Installer(db).uninstall();
    } catch (Exception ex) {
      System.err.println("failed uninstalling database");
    }
    new Installer(db).install();

    int ownerId = Users.createUserId(db, "owner@adama.games");

    System.err.println("CREATED OWNER:" + ownerId);
    int ideId = Spaces.createSpace(db, ownerId, "ide");
    {
      ObjectNode plan = Json.newJsonObject();
      plan.putObject("versions").put("file", getIDE());
      plan.put("default", "file");
      plan.putArray("plan");
      String planJson = plan.toString();
      Spaces.setPlan(db, ideId, planJson, "hash");
    }
    int billingId = Spaces.createSpace(db, ownerId, "billing");
    {
      ObjectNode plan = Json.newJsonObject();
      plan.putObject("versions").put("file", getBilling());
      plan.put("default", "file");
      plan.putArray("plan");
      String planJson = plan.toString();
      Spaces.setPlan(db, billingId, planJson, "hash");
    }

    backendGlobalConfigs = new JsonConfig[] {
        assembleConfig(Role.Adama, "central"),
        assembleConfig(Role.Adama, "central"),
        assembleConfig(Role.Adama, "central")
    };

    globalBackends = new Backend[backendGlobalConfigs.length];
    for (int k = 0; k < globalBackends.length; k++) {
      globalBackends[k] = Backend.run(backendGlobalConfigs[k]);
    }
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Frontend.run(frontendGlobalConfig);
        } catch (Exception ex) {

        }
      }
    }).start();

    clients = SimpleExecutor.create("clients");

    clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(Json.newJsonObject())));
    globalClientPool = new MultiWebClientRetryPool(clients, clientBase, new MultiWebClientRetryPoolMetrics(new NoOpMetricsFactory()), new MultiWebClientRetryPoolConfig(new ConfigObject(Json.newJsonObject())), ConnectionReady.TRIVIAL, "ws://127.0.0.1:" + globalWebPort + "/~s");
    this.globalClient = new SelfClient(globalClientPool);
    System.out.println("----------------------------------------------");
    System.out.println("GLOBAL ONLINE :http://127.0.0.1:" + globalWebPort);
    System.out.println("----------------------------------------------");

    ClientProbeRequest cpr = new ClientProbeRequest();
    cpr.identity = "anonymous:hello_adama";
    CountDownLatch pool = new CountDownLatch(1);
    for (int k = 0; k < 5 && pool.getCount() > 0; k++) {
      globalClient.probe(cpr, new Callback<ClientSimpleResponse>() {
        @Override
        public void success(ClientSimpleResponse value) {
          pool.countDown();
          System.out.println("---------------");
          System.out.println("[PROBE SUCCESS]");
          System.out.println("---------------");
        }

        @Override
        public void failure(ErrorCodeException ex) {
        }
      });
      pool.await(1000, TimeUnit.MILLISECONDS);
    }
  }

  private ObjectNode getAwsConfig() throws Exception {
    File configFile = new File("./system/aws.config.json");
    if (!configFile.exists()) {
      configFile = new File("./aws.config.json");
    }
    return Json.parseJsonObject(Files.readString(configFile.toPath()));
  }

  private String getIdentityFilename() {
    File configFile = new File("./system/localhost.identity");
    if (!configFile.exists()) {
      return "./localhost.identity";
    }
    return "./system/localhost.identity";
  }

  private ObjectNode getDbConfig() throws Exception {
    File configFile = new File("./system/test.mysql.json");
    if (!configFile.exists()) {
      configFile = new File("./test.mysql.json");
    }
    ObjectNode root = Json.parseJsonObject(Files.readString(configFile.toPath()));
    if (root.has("any")) {
      return (ObjectNode) root.get("any");
    }
    return root;
  }

  public JsonConfig assembleConfig(Role role, String region) throws Exception {
    int servicePort = port++;
    int monitorPort = port++;
    int webPort = port++;
    int redirectPort = port++;

    ObjectNode config = Json.newJsonObject();
    config.put("push-email", "no-reply@adama-platform.com");
    ObjectNode aws = getAwsConfig();
    ObjectNode db = getDbConfig();
    config.set("aws", aws);
    config.set("db", db);
    config.put("identity-filename", getIdentityFilename());
    aws.put("archive", "archive-" + servicePort);
    config.put("adama-port", servicePort);
    config.put("monitoring-" + role.name + "-port", monitorPort);
    ObjectNode web = config.putObject("web");
    web.put("http-port", webPort);
    web.put("http-redirect-port", redirectPort);
    // TODO: region-endpoint
    config.put("environment", "test");
    config.put("global", "central".equals(region));
    config.putArray("regional-public-keys"); // .add
    config.putArray("super-public-keys"); //.add
    config.put("region", "central");
    config.put("overlord-region-global", "central");
    config.put("role", role.name);
    config.put("master-key", masterKey);
    config.put("monitoring-system", "no-op");
    config.put("caravan-root", "caravan-" + servicePort);

    byte[] rawPrivateKey = Base64.getDecoder().decode(regionalPrivateKey);
    PrivateKey regionalPrivateKey = KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(rawPrivateKey));
    config.put("regional-identity", Jwts.builder().subject("127.0.0.1").issuer("region").signWith(regionalPrivateKey).compact());
    return new JsonConfig(config);
  }

  public void shutdown() {
    globalClientPool.shutdown();
    clients.shutdown();
    clientBase.shutdown();
    scrub();
  }

  private void scrub(File root) {
    for (File f : root.listFiles()) {
      if (f.isDirectory()) {
        scrub(f);
      }
      f.delete();
    }
  }

  private void scrub() {
    String list = new File("./system").exists() ? "./system" : "./";
    if (new File(list + "/aws.config.json").exists()) {
      for (File p : new File(list).listFiles()) {
        if (p.getName().startsWith("caravan-25") || p.getName().startsWith("archive-25") || p.getName().equals("logs") || p.getName().equals("billing")) {
          scrub(p);
        }
      }
    }
  }

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
}
