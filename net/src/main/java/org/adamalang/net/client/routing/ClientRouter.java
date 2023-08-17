/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.routing;

import org.adamalang.common.*;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.routing.finder.FinderServiceRouter;
import org.adamalang.runtime.sys.capacity.MachinePicker;
import org.adamalang.net.client.routing.cache.AggregatedCacheRouter;
import org.adamalang.net.client.routing.finder.ReactiveCacheMachinePicker;
import org.adamalang.runtime.data.FinderService;

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
