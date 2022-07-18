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
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.common.net.NetBase;
import org.adamalang.net.client.contracts.*;
import org.adamalang.net.client.proxy.ProxyDataService;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.net.client.routing.reactive.ReativeRoutingEngine;
import org.adamalang.net.client.sm.Connection;
import org.adamalang.net.client.sm.ConnectionBase;
import org.adamalang.net.client.sm.LinearConnectionStateMachine;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebPutRaw;
import org.adamalang.runtime.sys.web.WebResponse;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** the front-door to talking to the gRPC client. */
public class Client {
  private final NetBase base;
  private final ClientMetrics metrics;
  private final ClientRouter router;
  private final InstanceClientFinder clientFinder;
  private final SimpleExecutor[] executors;
  private final Random rng;
  private final ClientConfig config;

  public Client(NetBase base, ClientConfig config, ClientMetrics metrics, ClientRouter router, HeatMonitor monitor) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
    this.router = router;
    this.clientFinder = new InstanceClientFinder(base, config, metrics, monitor, SimpleExecutorFactory.DEFAULT, 4, router.engine, ExceptionLogger.FOR(Client.class));
    this.executors = SimpleExecutorFactory.DEFAULT.makeMany("connections", 2);
    this.rng = new Random();
  }

  public ReativeRoutingEngine routing() {
    return router.engine;
  }

  public void getDeploymentTargets(String space, Consumer<String> stream) {
    router.engine.list(space, targets -> clientFinder.findCapacity(targets, (set) -> {
      for (String target : set) {
        stream.accept(target);
      }
    }, 3));
  }

  public void getProxy(String target, Callback<ProxyDataService> callback) {
    clientFinder.find(target, new Callback<InstanceClient>() {
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
        router.engine.list(space, (targets) -> {
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
    clientFinder.find(target, new Callback<>() {
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
    router.engine.random(target -> {
      clientFinder.find(target, new Callback<>() {
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
    return (targets) -> clientFinder.sync(new TreeSet<>(targets));
  }

  public void reflect(String space, String key, Callback<String> callback) {
    router.engine.get(new Key(space, key), new RoutingSubscriber() {
      @Override
      public void onRegion(String region) {
        // TODO: proxy to another region
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }

      @Override
      public void onMachine(String machine) {
        clientFinder.find(machine, new Callback<>() {
          @Override
          public void success(InstanceClient client) {
            client.reflect(space, key, new Callback<>() {
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
      }
    });
  }

  public void webGet(String space, String key, WebGet request, Callback<WebResponse> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance mInstance = metrics.client_webget_found_machine.start();
    router.routerForDocuments.get(new Key(space, key), new RoutingSubscriber() {
      @Override
      public void onRegion(String region) {
        mInstance.failure(ErrorCodes.ADAMA_NET_WEBGET_FOUND_REGION_RATHER_THAN_MACHINE);
        callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_WEBGET_FOUND_REGION_RATHER_THAN_MACHINE));
      }

      @Override
      public void onMachine(String machine) {
        mInstance.success();
        clientFinder.find(machine,  new Callback<InstanceClient>() {
          @Override
          public void success(InstanceClient client) {
            client.webGet(space, key, request, callback);
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
  }

  public void webPut(String space, String key, WebPut request, Callback<WebResponse> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance mInstance = metrics.client_webput_found_machine.start();
    router.routerForDocuments.get(new Key(space, key), new RoutingSubscriber() {
      @Override
      public void onRegion(String region) {
        mInstance.failure(ErrorCodes.ADAMA_NET_WEBPUT_FOUND_REGION_RATHER_THAN_MACHINE);
        callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_WEBPUT_FOUND_REGION_RATHER_THAN_MACHINE));
      }

      @Override
      public void onMachine(String machine) {
        mInstance.success();
        clientFinder.find(machine,  new Callback<InstanceClient>() {
          @Override
          public void success(InstanceClient client) {
            client.webPut(space, key, request, callback);
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
  }


  public void create(String ip, String origin, String agent, String authority, String space, String key, String entropy, String arg, Callback<Void> callback) {
    RequestResponseMonitor.RequestResponseMonitorInstance mInstance = metrics.client_create_found_machine.start();
    router.routerForDocuments.get(new Key(space, key), new RoutingSubscriber() {
      @Override
      public void onRegion(String region) {
        mInstance.failure(ErrorCodes.ADAMA_NET_CREATE_FOUND_REGION_RATHER_THAN_MACHINE);
        callback.failure(new ErrorCodeException(ErrorCodes.ADAMA_NET_CREATE_FOUND_REGION_RATHER_THAN_MACHINE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }

      @Override
      public void onMachine(String machine) {
        mInstance.success();
        clientFinder.find(machine,  new Callback<InstanceClient>() {
          @Override
          public void success(InstanceClient client) {
            client.create(ip, origin, agent, authority, space, key, entropy, arg, callback);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
      }
    });
  }

  public Connection connect(String ip, String origin, String agent, String authority, String space, String key, String viewerState, String assetKey, SimpleEvents events) {
    ConnectionBase base = new ConnectionBase(config, metrics, router.routerForDocuments, clientFinder, executors[rng.nextInt(executors.length)]);
    Connection connection = new Connection(base, ip, origin, agent, authority, space, key, viewerState, assetKey, events);
    connection.open();
    return connection;
  }

  /** Experimental: simplified connection state machine */
  public LinearConnectionStateMachine simpleConnect(String ip, String origin, String agent, String authority, String space, String key, String viewerState, String assetKey, SimpleEvents events) {
    ConnectionBase base = new ConnectionBase(config, metrics, router.routerForDocuments, clientFinder, executors[rng.nextInt(executors.length)]);
    LinearConnectionStateMachine connection = new LinearConnectionStateMachine(base, ip, origin, agent, authority, space, key, viewerState, assetKey, events);
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
    AwaitHelper.block(clientFinder.shutdown(), 1000);
    router.shutdown();
  }
}
