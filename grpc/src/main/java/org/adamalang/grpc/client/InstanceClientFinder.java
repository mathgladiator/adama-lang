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

import org.adamalang.common.*;
import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.client.contracts.QueueAction;
import org.adamalang.grpc.client.routing.RoutingEngine;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** You ask it for clients, and you get clients */

public class InstanceClientFinder {
  /** tests to write
   + kill finder once a client is connected
   + reject queue
   + add after killing queue
   + too many items in the queue
   + fill queue with things, then disconnect the server on "finder-lost"; use sync
   */
  private final MachineIdentity identity;
  private final RoutingEngine engine;
  private final HashMap<String, InstanceClientProxy> clients;
  private final SimpleExecutor[] clientExecutors;
  private final SimpleExecutor mapExecutor;
  private final ExceptionLogger logger;
  private final Random rng;
  private final AtomicBoolean alive;

  public InstanceClientFinder(
      MachineIdentity identity,
      SimpleExecutorFactory threadFactory,
      int nThreads,
      RoutingEngine engine,
      ExceptionLogger logger) {
    this.identity = identity;
    this.engine = engine;
    this.clients = new HashMap<>();
    this.clientExecutors = threadFactory.makeMany("instance-client-finder", nThreads);
    this.mapExecutor = threadFactory.makeSingle("instance-client-finder-main");
    this.logger = logger;
    this.rng = new Random();
    this.alive = new AtomicBoolean(true);
  }

  public CountDownLatch shutdown() {
    CountDownLatch latch = new CountDownLatch(clientExecutors.length + 1);
    mapExecutor.execute(
        new NamedRunnable("finder-shutting-down") {
          @Override
          public void execute() throws Exception {
            // close all the connections
            for (InstanceClientProxy proxy : clients.values()) {
              proxy.executor.execute(
                  new NamedRunnable("shutdown-proxy") {
                    @Override
                    public void execute() throws Exception {
                      proxy.close();
                    }
                  });
            }
            // close down all the executors
            for (int k = 0; k < clientExecutors.length; k++) {
              SimpleExecutor executor = clientExecutors[k];
              executor.execute(
                  new NamedRunnable("kill-executor", Integer.toString(k)) {
                    @Override
                    public void execute() throws Exception {
                      executor.shutdown();
                      latch.countDown();
                    }
                  });
            }
            // shutdown the map executor
            mapExecutor.shutdown();
            latch.countDown();
          }
        });
    return latch;
  }

  public void sync(TreeSet<String> targets) {
    for (String target : targets) {
      find(target, null);
    }
    mapExecutor.execute(new NamedRunnable("finder-cleaning") {
      @Override
      public void execute() throws Exception {
        Iterator<Map.Entry<String, InstanceClientProxy>> it = clients.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<String, InstanceClientProxy> entry = it.next();
          if (!targets.contains(entry.getKey())) {
            entry.getValue().close();
            it.remove();
          }
        }
      }
    });
  }

  public void findCapacity(TreeSet<String> existing, Consumer<TreeSet<String>> capacity, int n) {
    if (existing.size() >= n) {
      capacity.accept(existing);
      return;
    }
    mapExecutor.execute(new NamedRunnable("finding-capacity") {
      @Override
      public void execute() throws Exception {
        TreeSet<String> results = new TreeSet<>(existing);
        String[] targets = new String[clients.size()];
        int at = 0;
        for (String target : clients.keySet()) {
          targets[at] = target;
          at++;
        }
        while (results.size() < n && results.size() < clients.size()) {
          results.add(targets[rng.nextInt(targets.length)]);
        }
        capacity.accept(results);
      }
    });
  }

  public void find(String target, QueueAction<InstanceClient> action) {
    mapExecutor.execute(
        new NamedRunnable("finder-find", target) {
          @Override
          public void execute() throws Exception {
            // look within the cache
            InstanceClientProxy cached = clients.get(target);
            if (cached == null) {
              // it doesn't exist, so create a proxy to hold the place
              cached = new InstanceClientProxy(target);
              // record the proxy if the above worked
              clients.put(target, cached);
            }
            if (action != null) {
              cached.add(action);
            }
          }
        });
  }

  private class InstanceClientProxy implements Lifecycle {
    private final SimpleExecutor executor;
    private InstanceClient createdClient;
    private InstanceClient client;
    private ArrayList<QueueAction<InstanceClient>> buffer;

    private InstanceClientProxy(String target) throws Exception {
      this.executor = clientExecutors[rng.nextInt(clientExecutors.length)];
      this.createdClient = new InstanceClient(identity, target, executor, this, logger);
      client = null;
      buffer = null;
    }

    @Override
    public void connected(InstanceClient client) {
      executor.execute(
          new NamedRunnable("finder-found", client.target) {
            @Override
            public void execute() throws Exception {
              if (alive.get()) {
                InstanceClientProxy.this.client = client;
                if (buffer != null) {
                  for (QueueAction<InstanceClient> action : buffer) {
                    action.execute(client);
                  }
                  buffer = null;
                }
              } else {
                client.close();
              }
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
          new NamedRunnable("finder-lost-client", client.target) {
            @Override
            public void execute() throws Exception {
              engine.remove(client.target);
              InstanceClientProxy.this.client = null;
              if (buffer != null) {
                for (QueueAction<InstanceClient> action : buffer) {
                  action.killDueToReject();
                }
                buffer = null;
              }
            }
          });
    }

    public void close() {
      createdClient.close();
      client = null;
      if (buffer != null) {
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
        if (createdClient.isAlive()) {
          if (buffer == null) {
            buffer = new ArrayList<>();
          }
          if (buffer.size() > 16) {
            action.killDueToReject();
          } else {
            buffer.add(action);
          }
        } else {
          action.killDueToReject();
        }
      }
    }
  }
}
