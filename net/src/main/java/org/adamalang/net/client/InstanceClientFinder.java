/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.net.NetBase;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.net.client.routing.RoutingEngine;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/** You ask it for clients, and you get clients */
public class InstanceClientFinder {
  private final NetBase base;
  private final ClientMetrics metrics;
  private final HeatMonitor monitor;
  private final RoutingEngine engine;
  private final ConcurrentHashMap<String, InstanceClient> clients;
  private final SimpleExecutor[] clientExecutors;
  private final SimpleExecutor mapExecutor;
  private final ExceptionLogger logger;
  private final Random rng;
  private final ClientConfig config;

  public InstanceClientFinder(NetBase base, ClientConfig config, ClientMetrics metrics, HeatMonitor monitor, SimpleExecutorFactory threadFactory, int nThreads, RoutingEngine engine, ExceptionLogger logger) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
    this.monitor = monitor;
    this.engine = engine;
    this.clients = new ConcurrentHashMap<>();
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
        for (InstanceClient client : clients.values()) {
          client.executor.execute(new NamedRunnable("shutdown-proxy") {
            @Override
            public void execute() throws Exception {
              client.close();
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
    mapExecutor.execute(new NamedRunnable("finder-cleaning") {
      @Override
      public void execute() throws Exception {
        for (String target : targets) {
          if (!clients.containsKey(target)) {
            clients.put(target, new InstanceClient(base, config, metrics, monitor, engine, target, clientExecutors[rng.nextInt(clientExecutors.length)], logger));
          }
        }
        Iterator<Map.Entry<String, InstanceClient>> it = clients.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<String, InstanceClient> entry = it.next();
          if (!targets.contains(entry.getKey())) {
            entry.getValue().close();
            it.remove();
          }
        }
      }
    });
  }

  public void find(String target, Callback<InstanceClient> action) {
    if (target == null) {
      action.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_FAILED_FIND_TARGET));
      return;
    }
    mapExecutor.execute(new NamedRunnable("finder-find", target) {
      @Override
      public void execute() throws Exception {
        InstanceClient client = clients.get(target);
        if (client != null) {
          action.success(client);
        } else {
          action.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_INVALID_TARGET));
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
}
