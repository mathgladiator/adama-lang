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
package org.adamalang.net.client.routing;

import org.adamalang.common.*;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.routing.finder.FinderServiceRouter;
import org.adamalang.runtime.sys.capacity.MachinePicker;
import org.adamalang.net.client.routing.cache.AggregatedCacheRouter;
import org.adamalang.net.client.routing.finder.ReactiveCacheMachinePicker;
import org.adamalang.runtime.data.FinderService;

@Deprecated
public class ClientRouter {
  private final SimpleExecutor executor;
  public final AggregatedCacheRouter engine;
  public final Router routerForDocuments;

  public ClientRouter(SimpleExecutor executor, AggregatedCacheRouter engine, Router routerForDocuments) {
    this.executor = executor;
    this.engine = engine;
    this.routerForDocuments = routerForDocuments;
  }

  public void shutdown() {
    AwaitHelper.block(executor.shutdown(), 1000);
  }

  public static ClientRouter REACTIVE(LocalRegionClientMetrics metrics) {
    SimpleExecutor executor = SimpleExecutor.create("routing-executor");
    AggregatedCacheRouter engine = new AggregatedCacheRouter(executor);
    return new ClientRouter(executor, engine, engine);
  }

  public static ClientRouter FINDER(LocalRegionClientMetrics metrics, FinderService finder, MachinePicker pickerFallback, String region) {
    SimpleExecutor executor = SimpleExecutor.create("routing-executor");
    AggregatedCacheRouter engine = new AggregatedCacheRouter(executor);
    MachinePicker picker = new ReactiveCacheMachinePicker(metrics, engine, pickerFallback);
    FinderServiceRouter routerForDocuments = new FinderServiceRouter(executor, finder, picker, region);
    return new ClientRouter(executor, engine, routerForDocuments);
  }
}
