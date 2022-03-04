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
import org.adamalang.common.queue.ItemAction;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.grpc.client.contracts.HeatMonitor;
import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.client.routing.RoutingEngine;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/** You ask it for clients, and you get clients */
public class InstanceClientFinder {
  /**
   * tests to write + kill finder once a client is connected + reject queue + add after killing
   * queue + too many items in the queue + fill queue with things, then disconnect the server on
   * "finder-lost"; use sync
   */
  private final ClientMetrics metrics;

  private final HeatMonitor monitor;
  private final MachineIdentity identity;
  private final RoutingEngine engine;
  private final HashMap<String, InstanceClientProxy> clients;
  private final SimpleExecutor[] clientExecutors;
  private final SimpleExecutor mapExecutor;
  private final ExceptionLogger logger;
  private final Random rng;

  public InstanceClientFinder(ClientMetrics metrics, HeatMonitor monitor, MachineIdentity identity, SimpleExecutorFactory threadFactory, int nThreads, RoutingEngine engine, ExceptionLogger logger) {
    this.metrics = metrics;
    this.monitor = monitor;
    this.identity = identity;
    this.engine = engine;
    this.clients = new HashMap<>();
    this.clientExecutors = threadFactory.makeMany("instance-client-finder", nThreads);
    this.mapExecutor = threadFactory.makeSingle("instance-client-finder-main");
    this.logger = logger;
    this.rng = new Random();
  }

  public CountDownLatch shutdown() {
    CountDownLatch latch = new CountDownLatch(clientExecutors.length + 1);
    mapExecutor.execute(new NamedRunnable("finder-shutting-down") {
      @Override
      public void execute() throws Exception {
        // close all the connections
        for (InstanceClientProxy proxy : clients.values()) {
          proxy.executor.execute(new NamedRunnable("shutdown-proxy") {
            @Override
            public void execute() throws Exception {
              proxy.close();
            }
          });
        }
        // close down all the executors
        for (int k = 0; k < clientExecutors.length; k++) {
          SimpleExecutor executor = clientExecutors[k];
          executor.execute(new NamedRunnable("kill-executor", Integer.toString(k)) {
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

  public void find(String target, ItemAction<InstanceClient> action) {
    mapExecutor.execute(new NamedRunnable("finder-find", target) {
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

  private class InstanceClientProxy implements Lifecycle {
    private final SimpleExecutor executor;
    private final InstanceClient createdClient;
    private final ItemQueue<InstanceClient> queue;
    private InstanceClient client;

    private InstanceClientProxy(String target) throws Exception {
      this.executor = clientExecutors[rng.nextInt(clientExecutors.length)];
      this.createdClient = new InstanceClient(identity, metrics, monitor, target, executor, this, logger);
      client = null;
      queue = new ItemQueue<>(this.executor, 16, 2500);
    }

    @Override
    public void connected(InstanceClient client) {
      executor.execute(new NamedRunnable("finder-proxy-connected", createdClient.target) {
        @Override
        public void execute() throws Exception {
          InstanceClientProxy.this.client = client;
          queue.ready(client);
        }
      });
    }

    @Override
    public void heartbeat(InstanceClient client, Collection<String> spaces) {
      engine.integrate(createdClient.target, spaces);
    }

    @Override
    public void disconnected(InstanceClient client) {
      executor.execute(new NamedRunnable("finder-proxy-disconnected", createdClient.target) {
        @Override
        public void execute() throws Exception {
          // note: connecting will fill the engine
          engine.remove(createdClient.target);
          InstanceClientProxy.this.client = null;
          queue.unready();
        }
      });
    }

    public void close() {
      executor.execute(new NamedRunnable("finder-proxy-close", createdClient.target) {
        @Override
        public void execute() throws Exception {
          createdClient.close();
          client = null;
          queue.unready();
          queue.nuke();
        }
      });
    }

    public void add(ItemAction<InstanceClient> action) {
      executor.execute(new NamedRunnable("finder-proxy-add", createdClient.target) {
        @Override
        public void execute() throws Exception {
          queue.add(action);
        }
      });
    }
  }
}
