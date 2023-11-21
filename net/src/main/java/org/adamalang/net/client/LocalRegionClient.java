/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.net.client;

import org.adamalang.common.*;
import org.adamalang.common.net.NetBase;
import org.adamalang.net.client.contracts.*;
import org.adamalang.net.client.routing.RoutingTableTarget;
import org.adamalang.net.client.sm.ConnectionBase;
import org.adamalang.net.client.sm.Connection;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.AuthResponse;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.runtime.sys.web.WebDelete;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** the front-door to talking to the gRPC client. */
public class LocalRegionClient {
  public final LocalRegionClientMetrics metrics;
  private final RoutingTableTarget table;
  private final InstanceClientFinder clientFinder;
  private final SimpleExecutor[] executors;
  private final Random rng;
  private final ClientConfig config;
  private final SimpleExecutor finderExecutor;
  public final LocalFinder finder;

  public LocalRegionClient(NetBase base, ClientConfig config, LocalRegionClientMetrics metrics, HeatMonitor monitor) {
    this.config = config;
    this.metrics = metrics;
    this.finderExecutor = SimpleExecutorFactory.DEFAULT.makeSingle("local-finder");
    this.table = new RoutingTableTarget(finderExecutor);
    this.clientFinder = new InstanceClientFinder(base, config, metrics, monitor, SimpleExecutorFactory.DEFAULT, 4, new RoutingTarget() {
      @Override
      public void integrate(String target, Collection<String> spaces) {
        table.integrate(target, spaces);
      }
    }, ExceptionLogger.FOR(LocalRegionClient.class));
    this.finder = new LocalFinder(table, clientFinder);
    this.executors = SimpleExecutorFactory.DEFAULT.makeMany("connections", 2);
    this.rng = new Random();
  }

  /** This is how the client learns of targets that are in the mesh */
  public Consumer<Collection<String>> getTargetPublisher() {
    return (targets) -> clientFinder.sync(new TreeSet<>(targets));
  }

  public void getMachineFor(Key key, Callback<String> callback) {
    table.get(key, callback);
  }

  public void getDeploymentTargets(String space, Consumer<String> stream) {
    table.list(space, targets -> clientFinder.findCapacity(targets, (set) -> {
      for (String target : set) {
        stream.accept(target);
      }
    }, 3));
  }

  public void waitForCapacity(String space, int timeout, Consumer<Boolean> done) {
    AtomicInteger time = new AtomicInteger(0);
    NamedRunnable task = new NamedRunnable("wait-for-capacity") {
      @Override
      public void execute() throws Exception {
        NamedRunnable self = this;
        table.list(space, (targets) -> {
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

  public void reflect(String machine, String space, String key, Callback<String> callback) {
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

  public void authorize(String machineToAsk, String ip, String origin, String space, String key, String username, String password, String new_password, Callback<String> callback) {
    clientFinder.find(machineToAsk,  new Callback<>() {
      @Override
      public void success(InstanceClient client) {
        client.authorize(ip, origin, space, key, username, password, new_password, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void authorization(String machineToAsk, String ip, String origin, String space, String key, String payload, Callback<AuthResponse> callback) {
    clientFinder.find(machineToAsk, new Callback<>() {
      @Override
      public void success(InstanceClient client) {
        client.authorization(ip, origin, space, key, payload, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }


  public void webGet(String machine, String space, String key, WebGet request, Callback<WebResponse> callback) {
    clientFinder.find(machine,  new Callback<>() {
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

  public void webOptions(String machine, String space, String key, WebGet request, Callback<WebResponse> callback) {
    clientFinder.find(machine,  new Callback<>() {
      @Override
      public void success(InstanceClient client) {
        client.webOptions(space, key, request, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void webPut(String machine, String space, String key, WebPut request, Callback<WebResponse> callback) {
    clientFinder.find(machine,  new Callback<>() {
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

  public void webDelete(String machine, String space, String key, WebDelete request, Callback<WebResponse> callback) {
    clientFinder.find(machine,  new Callback<InstanceClient>() {
      @Override
      public void success(InstanceClient client) {
        client.webDelete(space, key, request, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void create(String machine, String ip, String origin, String agent, String authority, String space, String key, String entropy, String arg, Callback<Void> callback) {
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

  public void delete(String machine, String ip, String origin, String agent, String authority, String space, String key, Callback<Void> callback) {
    clientFinder.find(machine,  new Callback<InstanceClient>() {
      @Override
      public void success(InstanceClient client) {
        client.delete(ip, origin, agent, authority, space, key, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void directSend(String machine, String ip, String origin, String agent, String authority, String space, String key, String marker, String channel, String message, Callback<Integer> callback) {
    clientFinder.find(machine,  new Callback<>() {
      @Override
      public void success(InstanceClient client) {
        client.directSend(ip, origin, agent, authority, space, key, marker, channel, message, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** Connect to a machine directly */
  public Connection connect(String machineToAsk, String ip, String origin, String agent, String authority, String space, String key, String viewerState, String assetKey, SimpleEvents events) {
    ConnectionBase base = new ConnectionBase(config, metrics, clientFinder, executors[rng.nextInt(executors.length)]);
    Connection connection = new Connection(base, machineToAsk, ip, origin, agent, authority, space, key, viewerState, assetKey, 2500, events);
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
  }
}
