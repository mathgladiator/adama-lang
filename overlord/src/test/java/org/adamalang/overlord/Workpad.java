/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.gossip.Engine;
import org.adamalang.gossip.EngineRole;
import org.adamalang.gossip.GossipMetricsImpl;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.overlord.grpc.OverlordTests;
import org.adamalang.runtime.data.Key;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Workpad {

  public static DataBaseConfig getLocalIntegrationConfig() throws Exception {
    return new DataBaseConfig(new ConfigObject(Json.parseJsonObject(Files.readString(new File("./overlord/test.mysql.json").toPath()))));
  }

  public static void main(String[] args) throws Exception {
    NoOpMetricsFactory metrics = new NoOpMetricsFactory();
    MachineIdentity identity = MachineIdentity.fromFile(OverlordTests.prefixForLocalhost());
    DataBase db = new DataBase(getLocalIntegrationConfig(), new DataBaseMetrics(metrics));

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
