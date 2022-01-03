package org.adamalang.grpc.client;

import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.client.contracts.QueueAction;
import org.adamalang.grpc.client.routing.RoutingEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/** You ask it for clients, and you get clients */
public class InstanceClientFinder {
  private final MachineIdentity identity;
  private final RoutingEngine engine;
  private final HashMap<String, InstanceClientProxy> clients;
  private final SimpleExecutor[] clientExecutors;
  private final SimpleExecutor mapExecutor;
  private final ExceptionLogger logger;
  private final Random rng;
  private final AtomicBoolean alive;

  public InstanceClientFinder(
      MachineIdentity identity, int nThreads, RoutingEngine engine, ExceptionLogger logger) {
    this.identity = identity;
    this.engine = engine;
    this.clients = new HashMap<>();
    this.clientExecutors = new SimpleExecutor[nThreads];
    for (int k = 0; k < nThreads; k++) {
      this.clientExecutors[k] = SimpleExecutor.create("instance-client-finder-" + k);
    }
    this.mapExecutor = SimpleExecutor.create("instance-client-finder-main");
    this.logger = logger;
    this.rng = new Random();
    this.alive = new AtomicBoolean(true);
  }

  public CountDownLatch shutdown() {
    CountDownLatch latch = new CountDownLatch(clientExecutors.length + 1);
    mapExecutor.execute(
        () -> {
          // close all the connections
          for (InstanceClientProxy proxy : clients.values()) {
            proxy.executor.execute(proxy::close);
          }
          // close down all the executors
          for (int k = 0; k < clientExecutors.length; k++) {
            SimpleExecutor executor = clientExecutors[k];
            executor.execute(
                () -> {
                  executor.shutdown();
                  latch.countDown();
                });
          }
          // shutdown the map executor
          mapExecutor.shutdown();
          latch.countDown();
        });
    return latch;
  }

  public void prime(Collection<String> targets) {
    for (String target : targets) {
      find(target, null);
    }
  }

  public void find(String target, QueueAction<InstanceClient> action) {
    mapExecutor.execute(
        () -> {
          // look within the cache
          InstanceClientProxy cached = clients.get(target);
          if (cached == null) {
            // it doesn't exist, so create a proxy to hold the place
            cached = new InstanceClientProxy();

            // create the client and have it feed the proxy
            try {
              new InstanceClient(identity, target, cached.executor, cached, logger);
              // record the proxy if the above worked
              clients.put(target, cached);
            } catch (Exception ex) {
              // TODO: figure out why this is the case
              ex.printStackTrace();
            }
          }
          if (action != null) {
            cached.add(action);
          }
        });
  }

  private class InstanceClientProxy implements Lifecycle {
    private final SimpleExecutor executor;
    private InstanceClient client;
    private ArrayList<QueueAction<InstanceClient>> buffer;

    private InstanceClientProxy() {
      executor = clientExecutors[rng.nextInt(clientExecutors.length)];
      client = null;
      buffer = null;
    }

    @Override
    public void connected(InstanceClient client) {
      executor.execute(
          () -> {
            if (alive.get()) {
              this.client = client;
              if (buffer != null) {
                for (QueueAction<InstanceClient> action : buffer) {
                  action.execute(client);
                }
                buffer = null;
              }
            } else {
              client.close();
            }
          });
    }

    @Override
    public void heartbeat(InstanceClient client, Collection<String> spaces) {
      if (alive.get()) {
        engine.integrate(client.target, spaces);
      }
    }

    @Override
    public void disconnected(InstanceClient client) {
      executor.execute(
          () -> {
            InstanceClientProxy.this.client = null;
          });
    }

    public void close() {
      if (client != null) {
        client.close();
      } else if (buffer != null) {
        for (QueueAction<InstanceClient> action : buffer) {
          action.killDueToReject();
        }
        buffer = null;
      }
    }

    public void add(QueueAction<InstanceClient> action) {
      if (client != null) {
        action.execute(client);
      } else {
        if (buffer == null) {
          buffer = new ArrayList<>();
        }
        // TODO: enforce limit
        buffer.add(action);
      }
    }
  }
}
