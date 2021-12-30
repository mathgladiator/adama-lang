package org.adamalang.grpc.client;

import java.util.concurrent.ScheduledExecutorService;

public class DurableClientBase {
  public final InstanceClientFinder finder;
  public final ScheduledExecutorService executor;

  public DurableClientBase(InstanceClientFinder finder, ScheduledExecutorService executor) {
    this.finder = finder;
    this.executor = executor;
  }

  public void shutdown() {
    executor.shutdown();
  }
}
