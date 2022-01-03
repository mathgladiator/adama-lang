package org.adamalang.grpc.client.routing;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.adamalang.runtime.contracts.Key;

import java.util.Collection;
import java.util.function.Consumer;

public class RoutingEngine {
  private final SimpleExecutor executor;
  private final RoutingTable table;
  private final int broadcastDelayOffset;
  private final int broadcastDelayJitter;
  private boolean broadcastInflight;

  public RoutingEngine(
      SimpleExecutor executor,
      SpaceTrackingEvents events,
      int broadcastDelayOffset,
      int broadcastDelayJitter) {
    this.executor = executor;
    this.table = new RoutingTable(events);
    this.broadcastInflight = false;
    this.broadcastDelayOffset = broadcastDelayOffset;
    this.broadcastDelayJitter = broadcastDelayJitter;
  }

  public void integrate(String target, Collection<String> newSpaces) {
    executor.execute(
        () -> {
          table.integrate(target, newSpaces);
          scheduleBroadcastWhileInExecutor();
        });
  }

  private void scheduleBroadcastWhileInExecutor() {
    if (!broadcastInflight) {
      broadcastInflight = true;
      executor.schedule(
          () -> {
            table.broadcast();
            broadcastInflight = false;
          },
          (int) (broadcastDelayOffset + Math.random() * broadcastDelayJitter));
    }
  }

  public void remove(String target) {
    executor.execute(
        () -> {
          table.remove(target);
          scheduleBroadcastWhileInExecutor();
        });
  }

  public void subscribe(Key key, Consumer<String> subscriber, Consumer<Runnable> onCancel) {
    executor.execute(
        () -> {
          onCancel.accept(table.subscribe(key, subscriber));
        });
  }
}
