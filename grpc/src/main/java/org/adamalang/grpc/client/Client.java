/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.SimpleExecutorFactory;
import org.adamalang.grpc.client.contracts.SimpleEvents;
import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.client.sm.Connection;
import org.adamalang.grpc.client.sm.ConnectionBase;

import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

public class Client {
  private final RoutingEngine engine;
  private final InstanceClientFinder finder;
  private final SimpleExecutor[] executors;
  private final Random rng;

  public Client(MachineIdentity identity) {
    this.engine =
        new RoutingEngine(
            SimpleExecutor.create("routing"),
            new SpaceTrackingEvents() {
              @Override
              public void gainInterestInSpace(String space) {}

              @Override
              public void shareTargetsFor(String space, Set<String> targets) {}

              @Override
              public void lostInterestInSpace(String space) {}
            },
            250,
            250);
    this.finder =
        new InstanceClientFinder(
            identity,
            SimpleExecutorFactory.DEFAULT,
            4,
            engine,
            (t, c) -> {
              t.printStackTrace();
            });
    this.executors = SimpleExecutorFactory.DEFAULT.makeMany("connections", 2);
    this.rng = new Random();
  }

  public Consumer<Collection<String>> getTargetPublisher() {
    return (targets) -> finder.sync(new TreeSet<>(targets));
  }

  public Connection create(
      String agent, String authority, String space, String key, SimpleEvents events) {
    ConnectionBase base =
        new ConnectionBase(engine, finder, executors[rng.nextInt(executors.length)]);
    Connection connection = new Connection(base, agent, authority, space, key, events);
    connection.open();
    return connection;
  }
}
