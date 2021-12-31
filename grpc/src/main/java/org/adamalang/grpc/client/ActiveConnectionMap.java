package org.adamalang.grpc.client;

import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.proto.InventoryRecord;
import org.adamalang.runtime.contracts.Key;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ActiveConnectionMap implements Lifecycle {
  private final ScheduledExecutorService executor;
  private TreeMap<String, InstanceClient> clients;
  private HashMap<Key, DEFUNCT_DurableConnectionStateMachine> connections;

  public ActiveConnectionMap() {
    this.executor = Executors.newSingleThreadScheduledExecutor();
    this.connections = new HashMap<>();
  }

  @Override
  public void connected(InstanceClient client) {
    executor.execute(() -> {
      clients.put(client.target, client);
    });
  }

  @Override
  public void heartbeat(InstanceClient client, Collection<InventoryRecord> records) {
    // do heat mapping, and then use this to build a routing table
  }

  @Override
  public void disconnected(InstanceClient client) {
    executor.execute(() -> {
      clients.remove(client.target);
    });
  }
}
