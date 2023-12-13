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
import com.lambdaworks.crypto.SCryptUtil;
import io.jsonwebtoken.Jwts;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.template.tree.T;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.Installer;
import org.adamalang.mysql.data.SpaceListingItem;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.mysql.model.Users;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.net.client.LocalRegionClient;
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
import org.junit.Assert;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

  private final Backend[] globalBackends;
  private final Frontend globalFrontend;

  private final SimpleExecutor clients;
  private final WebClientBase clientBase;
  private final MultiWebClientRetryPool globalClientPool;

  public final SelfClient globalClient;

  public final List<String> backendHosts;

  public TestEnvironment() throws Exception {
    long started = System.currentTimeMillis();
    try {
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

      backendGlobalConfigs = new JsonConfig[]{assembleConfig(Role.Adama, "central"), assembleConfig(Role.Adama, "central"), assembleConfig(Role.Adama, "central")};

      globalBackends = new Backend[backendGlobalConfigs.length];
      for (int k = 0; k < globalBackends.length; k++) {
        globalBackends[k] = Backend.run(backendGlobalConfigs[k]);
      }
      AtomicReference<Frontend> frontendRef = new AtomicReference<>(null);
      CountDownLatch gotFrontend = new CountDownLatch(1);
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            Frontend fe = Frontend.run(frontendGlobalConfig);
            frontendRef.set(fe);
            gotFrontend.countDown();
            fe.run();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }).start();

      Assert.assertTrue(gotFrontend.await(100000, TimeUnit.MILLISECONDS));
      globalFrontend = frontendRef.get();

      clients = SimpleExecutor.create("clients");

      clientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(Json.newJsonObject())));
      globalClientPool = new MultiWebClientRetryPool(clients, clientBase, new MultiWebClientRetryPoolMetrics(new NoOpMetricsFactory()), new MultiWebClientRetryPoolConfig(new ConfigObject(Json.newJsonObject())), ConnectionReady.TRIVIAL, "ws://127.0.0.1:" + globalWebPort + "/~s");
      this.globalClient = new SelfClient(globalClientPool);
      System.out.println("----------------------------------------------");
      System.out.println("GLOBAL ONLINE :http://127.0.0.1:" + globalWebPort);
      System.out.println("----------------------------------------------");

      List<String> hosts = new ArrayList<>();
      while (hosts.size() != 3) {
        hosts = Hosts.listHosts(db, "central", "adama");
        Thread.sleep(50);
      }
      backendHosts = hosts;

      for (String host : hosts) {
        AtomicBoolean success = new AtomicBoolean(false);
        int attempts = 6;
        while (!success.get() && attempts > 0) {
          attempts--;
          System.out.println("touching:" + host);
          CountDownLatch latch = new CountDownLatch(1);
          globalFrontend.local.find(host, new Callback<>() {
            @Override
            public void success(InstanceClient client) {
              if (client != null) {
                try {
                  if (client.ping(500)) {
                    success.set(true);
                  }
                } catch (Exception ex) {
                  System.err.println("failed ping:" + host);
                }
              }
              latch.countDown();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              latch.countDown();
            }
          });
          latch.await(250, TimeUnit.MILLISECONDS);
        }
        Assert.assertTrue(attempts > 0);
      }

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
    } finally {
      System.out.println("[test environment took:" + (System.currentTimeMillis() - started) + "ms");
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

  public String getIdentity(String email) throws Exception {
    long started = System.currentTimeMillis();
    try {
      ClientInitSetupAccountRequest cis = new ClientInitSetupAccountRequest();
      cis.email = email;
      {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        globalClient.initSetupAccount(cis, new Callback<ClientSimpleResponse>() {
          @Override
          public void success(ClientSimpleResponse value) {
            System.err.println("-=[INIT ACCOUNT]=-");
            latch.countDown();
            success.set(true);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("FAILED:" + ex.code);
            latch.countDown();
          }
        });
        Assert.assertTrue(latch.await(60000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(success.get());
      }

      // POKE BEHIND THE SCENES TO SET THE PASSWORD
      int userId = Users.getUserId(db, email);
      Users.setPasswordHash(db, userId, SCryptUtil.scrypt("password", 16384, 8, 1));
      AtomicReference<String> identity = new AtomicReference<>(null);
      {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        ClientAccountLoginRequest calr = new ClientAccountLoginRequest();
        calr.email = email;
        calr.password = "password";
        globalClient.accountLogin(calr, new Callback<ClientInitiationResponse>() {
          @Override
          public void success(ClientInitiationResponse value) {
            latch.countDown();
            identity.set(value.identity);
            success.set(true);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            latch.countDown();
          }
        });
        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        Assert.assertTrue(success.get());
      }
      return identity.get();
    } finally {
      System.err.println("getIdentity(\"" + email + "\") took " + (System.currentTimeMillis() - started) + "ms");
    }
  }
}
