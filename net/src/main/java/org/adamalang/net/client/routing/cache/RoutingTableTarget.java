/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
