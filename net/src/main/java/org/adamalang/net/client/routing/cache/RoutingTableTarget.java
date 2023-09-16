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
import org.adamalang.net.client.contracts.RoutingTarget;

import java.util.Collection;

/** converts a RoutingTable into a thread-safe RoutingTarget using an executor */
public class RoutingTableTarget implements RoutingTarget {
  public final RoutingTable table;
  public final SimpleExecutor executor;

  public RoutingTableTarget(SimpleExecutor executor) {
    this.executor = executor;
    this.table = new RoutingTable();
  }

  @Override
  public void integrate(String target, Collection<String> spaces) {
    executor.execute(new NamedRunnable("local-finder-integrate", target) {
      @Override
      public void execute() throws Exception {
        table.integrate(target, spaces);
      }
    });
  }

  public void remove(String target) {
    executor.execute(new NamedRunnable("local-finder-remove-target") {
      @Override
      public void execute() throws Exception {
        table.remove(target);
      }
    });
  }
}
