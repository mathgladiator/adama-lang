package org.adamalang.grpc.client.sm;

import org.adamalang.grpc.client.InstanceClientFinder;
import org.adamalang.grpc.client.routing.RoutingEngine;

import java.util.concurrent.ScheduledExecutorService;

/** each state machine has some common ground, and we form a base around that */
public class Base {
  // how to map keys to targets;
  public final RoutingEngine engine;

  // how we turn targets into clients
  public final InstanceClientFinder mesh;

  // how we handle thread safety and time
  public final ScheduledExecutorService scheduler;

  public Base(RoutingEngine engine, InstanceClientFinder mesh, ScheduledExecutorService scheduler) {
    this.engine = engine;
    this.mesh = mesh;
    this.scheduler = scheduler;
  }
}
