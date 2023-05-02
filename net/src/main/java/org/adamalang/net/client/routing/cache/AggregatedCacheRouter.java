/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.routing.cache;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.contracts.RoutingCallback;
import org.adamalang.net.client.contracts.RoutingTarget;
import org.adamalang.net.client.routing.Router;
import org.adamalang.runtime.data.Key;

import java.util.Collection;
import java.util.TreeSet;
import java.util.function.Consumer;

public class AggregatedCacheRouter implements RoutingTarget, Router {
  private final SimpleExecutor executor;
  private final RoutingTable table;

  public AggregatedCacheRouter(SimpleExecutor executor) {
    this.executor = executor;
    this.table = new RoutingTable();
  }

  public void list(String space, Consumer<TreeSet<String>> callback) {
    executor.execute(new NamedRunnable("listing-targets") {
      @Override
      public void execute() throws Exception {
        callback.accept(table.targetsFor(space));
      }
    });
  }

  public void random(Consumer<String> callback) {
    executor.execute(new NamedRunnable("find-random-target") {
      @Override
      public void execute() throws Exception {
        callback.accept(table.random());
      }
    });
  }

  @Override
  public void get(Key key, RoutingCallback callback) {
    executor.execute(new NamedRunnable("get", key.space, key.key) {
      @Override
      public void execute() throws Exception {
        callback.onMachine(table.get(key.space, key.key));
      }
    });
  }

  @Override
  public void integrate(String target, Collection<String> newSpaces) {
    executor.execute(new NamedRunnable("routing-integrate", target) {
      @Override
      public void execute() throws Exception {
        table.integrate(target, newSpaces);
      }
    });
  }

  public void remove(String target) {
    executor.execute(new NamedRunnable("routing-remove", target) {
      @Override
      public void execute() throws Exception {
        table.remove(target);
      }
    });
  }
}
