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

@Deprecated
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
