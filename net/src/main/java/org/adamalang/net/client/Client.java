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

import org.adamalang.common.*;
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.net.NetBase;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.net.client.contracts.MeteringStream;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.net.client.contracts.SpaceTrackingEvents;
import org.adamalang.net.client.proxy.ProxyDataService;
import org.adamalang.net.client.routing.RoutingEngine;
import org.adamalang.net.client.sm.Connection;
import org.adamalang.net.client.sm.ConnectionBase;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** the front-door to talking to the gRPC client. */
public class Client {
  private final NetBase base;
  private final ClientMetrics metrics;
  private final SimpleExecutor routingExecutor;
  private final RoutingEngine engine;
  private final InstanceClientFinder finder;
  private final SimpleExecutor[] executors;
  private final Random rng;
  private final ClientConfig config;

  public Client(NetBase base, ClientConfig config, ClientMetrics metrics, HeatMonitor monitor) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
    this.routingExecutor = SimpleExecutor.create("routing");
    this.engine = new RoutingEngine(metrics, routingExecutor, new SpaceTrackingEvents() {
      @Override
      public void gainInterestInSpace(String space) {
      }

      @Override
      public void shareTargetsFor(String space, Set<String> targets) {
      }

      @Override
      public void lostInterestInSpace(String space) {
      }
    }, 250, 250);
    this.finder = new InstanceClientFinder(base, config, metrics, monitor, SimpleExecutorFactory.DEFAULT, 4, engine, ExceptionLogger.FOR(Client.class));
    this.executors = SimpleExecutorFactory.DEFAULT.makeMany("connections", 2);
    this.rng = new Random();
  }

  public RoutingEngine routing() {
    return engine;
  }

  public void getDeploymentTargets(String space, Consumer<String> stream) {
    engine.list(space, targets -> finder.findCapacity(targets, (set) -> {
      for (String target : set) {
        stream.accept(target);
      }
    }, 3));
  }

  public void getProxy(String target, Callback<ProxyDataService> callback) {
    finder.find(target, new Callback<InstanceClient>() {
      @Override
      public void success(InstanceClient value) {
        callback.success(value.getProxy());
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void waitForCapacity(String space, int timeout, Consumer<Boolean> done) {
    AtomicInteger time = new AtomicInteger(0);
    NamedRunnable task = new NamedRunnable("wait-for-capacity") {
      @Override
      public void execute() throws Exception {
        NamedRunnable self = this;
        engine.list(space, (targets) -> {
          if (targets.size() == 0) {
            if (time.get() < timeout) {
              int step = (int) (125 + Math.random() * 125);
              time.set(time.get() + step);
              executors[rng.nextInt(executors.length)].schedule(self, step);
            } else {
              done.accept(false);
            }
          } else {
            done.accept(true);
          }
        });
      }
    };
    executors[rng.nextInt(executors.length)].execute(task);
  }

  public void notifyDeployment(String target, String space) {
    finder.find(target, new Callback<>() {
      @Override
      public void success(InstanceClient value) {
        value.scanDeployments(space, new Callback<>() {
          @Override
          public void success(Void value) {
            metrics.client_notify_deploy_success.run();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            metrics.client_notify_deploy_failure_do.run();
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        metrics.client_notify_deploy_failure_find.run();
      }
    });
  }

  public void randomMeteringExchange(MeteringStream metering) {
    engine.random(target -> {
      finder.find(target, new Callback<>() {
        @Override
        public void success(InstanceClient value) {
          value.startMeteringExchange(metering);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          metering.failure(ex.code);
        }
      });
    });
  }

  public Consumer<Collection<String>> getTargetPublisher() {
    return (targets) -> finder.sync(new TreeSet<>(targets));
  }

  public void reflect(String space, String key, Callback<String> callback) {
    engine.get(space, key, (target) -> {
      finder.find(target, new Callback<InstanceClient>() {
        @Override
        public void success(InstanceClient client) {
          client.reflect(space, key, new Callback<String>() {
            @Override
            public void success(String value) {
              callback.success(value);
            }

            @Override
            public void failure(ErrorCodeException ex) {
              callback.failure(ex);
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    });
  }

  public void create(String ip, String origin, String agent, String authority, String space, String key, String entropy, String arg, Callback<Void> callback) {
    ItemActionMonitor.ItemActionMonitorInstance mInstance = metrics.client_create.start();
    engine.get(space, key, (target) -> {
      finder.find(target, new Callback<InstanceClient>() {
        @Override
        public void success(InstanceClient client) {
          client.create(ip, origin, agent, authority, space, key, entropy, arg, callback);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    });
  }

  public Connection connect(String ip, String origin, String agent, String authority, String space, String key, String viewerState, SimpleEvents events) {
    ConnectionBase base = new ConnectionBase(config, metrics, engine, finder, executors[rng.nextInt(executors.length)]);
    Connection connection = new Connection(base, ip, origin, agent, authority, space, key, viewerState, events);
    connection.open();
    return connection;
  }

  public void shutdown() {
    ArrayList<CountDownLatch> latches = new ArrayList<>(executors.length);
    for (SimpleExecutor executor : executors) {
      latches.add(executor.shutdown());
    }
    for (CountDownLatch latch : latches) {
      AwaitHelper.block(latch, 500);
    }
    AwaitHelper.block(finder.shutdown(), 1000);
    AwaitHelper.block(routingExecutor.shutdown(), 1000);
  }
}
