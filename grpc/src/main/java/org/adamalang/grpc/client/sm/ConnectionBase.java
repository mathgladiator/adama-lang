package org.adamalang.grpc.client.sm;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.grpc.client.InstanceClientFinder;
import org.adamalang.grpc.client.routing.RoutingEngine;

/** each state machine has some common ground, and we form a base around that */
public class ConnectionBase {
  // how to map keys to targets;
  public final RoutingEngine engine;

  // how we turn targets into clients
  public final InstanceClientFinder mesh;

  // how we handle thread safety and time
  public final SimpleExecutor executor;

  public ConnectionBase(RoutingEngine engine, InstanceClientFinder mesh, SimpleExecutor executor) {
    this.engine = engine;
    this.mesh = mesh;
    this.executor = executor;
  }
}
