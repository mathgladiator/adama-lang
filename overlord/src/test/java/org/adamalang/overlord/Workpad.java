/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.gossip.Engine;
import org.adamalang.gossip.EngineRole;
import org.adamalang.gossip.GossipMetricsImpl;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.deployments.Deployments;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.mysql.frontend.data.SpaceInfo;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.overlord.grpc.OverlordTests;
import org.adamalang.runtime.data.Key;

import javax.xml.crypto.Data;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Workpad {

  public static DataBaseConfig getLocalIntegrationConfig() throws Exception {
    return new DataBaseConfig(new ConfigObject(Json.parseJsonObject(Files.readString(new File("./overlord/test.mysql.json").toPath()))), "any");
  }

  public static void main(String[] args) throws Exception {
    NoOpMetricsFactory metrics = new NoOpMetricsFactory();
    MachineIdentity identity = MachineIdentity.fromFile(OverlordTests.prefixForLocalhost());
    DataBase db = new DataBase(getLocalIntegrationConfig(), new DataBaseMetrics(metrics, "wordpad"));

    /*
    int userId = Users.getOrCreateUserId(db, "test@test.com");
    System.err.println("userId=" + userId);
    int spaceId = Spaces.createSpace(db, userId, "chat");
    System.err.println("spaceId=" + spaceId);
    */
    /*
    SpaceInfo info = Spaces.getSpaceInfo(db, "chat");
    System.err.println("spaceId=" + info.id + "/ownerId=" + info.owner);

    ObjectNode plan = Json.newJsonObject();
    plan.putObject("versions").put("file", "@static {\n" + "  // anyone can create\n" + "  create(who) { return true; }\n" + "  invent(who) { return true; }\n" + "  maximum_history = 100;\n" + "}\n" + "\n" + "// let anyone into the document\n" + "@connected (who) {\n" + "  return true;\n" + "}\n" + "\n" + "// the lines of chat\n" + "record Line {\n" + "  public client who;\n" + "  public string what;\n" + "  public long when;\n" + "}\n" + "\n" + "// the chat table\n" + "table<Line> _chat;\n" + "\n" + "// how someone communicates to the document\n" + "message Say {\n" + "  string what;\n" + "}\n" + "\n" + "// the \"channel\" which enables someone to say something\n" + "channel say(client who, Say what) {\n" + "  // ingest the line into the chat\n" + "  _chat <- {who:who, what:what.what, when: Time.now()};\n" + "\n" + "  (iterate _chat order by when desc offset 5).delete();\n" + "}\n" + "\n" + "// emit the data out\n" + "view bool ordering;\n" + "\n" + "bubble<who, viewer> chat = viewer.ordering ? (iterate _chat order id desc) : (iterate _chat order id asc);\n" + "\n" + "message Flux {\n" + "}\n" + "\n" + "channel flux(client w, Flux f) {\n" + "  (iterate _chat where who==w).what += \"x\";\n" + "}");
    plan.put("default", "file");
    plan.putArray("plan");
    String planJson = plan.toString();

    // hash the plan
    MessageDigest digest = Hashing.md5();
    digest.digest(planJson.getBytes(StandardCharsets.UTF_8));
    String hash = Hashing.finishAndEncode(digest);
    // Change the master plan
    Spaces.setPlan(db, info.id, planJson, hash);
    Deployments.deploy(db, "chat", identity.ip + ":8001", hash, planJson);
    */

    Engine engine = new Engine(identity, TimeSource.REAL_TIME, new HashSet<>(Collections.singleton("127.0.0.1:8002")), 8100, 8101, new GossipMetricsImpl(metrics), EngineRole.Node);
    engine.start();
    CountDownLatch membership = new CountDownLatch(2);
    engine.subscribe("adama", new Consumer<Collection<String>>() {
      @Override
      public void accept(Collection<String> targets) {
        System.err.println(targets);
        membership.countDown();
      }
    });
    System.err.println("Found:" + membership.await(5000, TimeUnit.MILLISECONDS));

    NetBase netBase = new NetBase(identity, 1, 2);
    ClientConfig clientConfig = new ClientConfig();
    ClientRouter router = ClientRouter.REACTIVE(new ClientMetrics(metrics));

    CountDownLatch foundMachine = new CountDownLatch(1);
    router.engine.subscribe(new Key("chat", "0"), new RoutingSubscriber() {
          @Override
          public void onRegion(String region) {

          }

          @Override
          public void onMachine(String machine) {
            if (machine != null) {
              foundMachine.countDown();
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        }, (cancel) -> {});

    Client client = new Client(netBase, clientConfig, new ClientMetrics(metrics), router, (target, cpu, memory) -> {});
    engine.subscribe("adama", client.getTargetPublisher());

    foundMachine.await(5000, TimeUnit.MILLISECONDS);

    client.create("1.2.3.4", "origin", "jeff", "adama", "chat", "caravan_0", null, "{}", new Callback<Void>() {
      @Override
      public void success(Void value) {
        System.err.println("create success");
      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.err.println("create failure: " + ex.code);
      }
    });
  }
}
