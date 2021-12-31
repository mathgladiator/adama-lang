package org.adamalang.grpc.client;

import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.MachineIdentity;
import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.client.contracts.TinyLifecycle;
import org.adamalang.grpc.proto.InventoryRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/** You ask it for clients, and you get clients */
public class InstanceClientCachePool {
  private final ScheduledExecutorService scheduler;
  private final HashMap<String, InstanceClientProxy> clients;

  private final MachineIdentity identity;
  private final ExceptionLogger logger;

  public InstanceClientCachePool(MachineIdentity identity, ScheduledExecutorService scheduler, ExceptionLogger logger) {
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
    this.clients = new HashMap<>();
    this.identity = identity;
    this.logger = logger;
  }

  private class InstanceClientProxy implements Lifecycle {
    private InstanceClient client;
    private ArrayList<TinyLifecycle> attached;

    private InstanceClientProxy() {
      client = null;
      attached = new ArrayList<>();
    }

    @Override
    public void connected(InstanceClient client) {
      scheduler.execute(() -> {
        this.client = client;
        for (TinyLifecycle lifecycle : attached) {
          lifecycle.connected(client, () -> {
            attached.remove(lifecycle);
          });
        }
      });
    }

    @Override
    public void heartbeat(InstanceClient client, Collection<InventoryRecord> records) {
      // handle this
      // TODO: send to routing table
    }

    public void add(TinyLifecycle lifecycle) {
      attached.add(lifecycle);
      if (client != null) {
        lifecycle.connected(client, () -> {
          scheduler.execute(() -> {
            attached.remove(lifecycle);
          });
        });
      }
    }

    @Override
    public void disconnected(InstanceClient client) {
      scheduler.execute(() -> {
        this.client = null;
        for (TinyLifecycle lifecycle : attached) {
          lifecycle.disconnect();
        }
        attached.clear();
      });
    }
  }

  public void prime(Collection<String> targets) {
    for (String target : targets) {
      connect(target, null);
    }
  }

  public void connect(String target, TinyLifecycle lifecycle) {
    scheduler.execute(
        () -> {
          InstanceClientProxy cached = clients.get(target);
          if (cached == null) {
            cached = new InstanceClientProxy();
            try {
              // TODO, the client should probably use a random scheduler
              new InstanceClient(identity, target, scheduler, cached, logger);
              clients.put(target, cached);
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
          if (lifecycle != null) {
            cached.add(lifecycle);
          }
        });
  }
}
