/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.routing;

import org.adamalang.common.*;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.routing.finder.FinderServiceRouter;
import org.adamalang.net.client.routing.finder.MachinePicker;
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

  public static ClientRouter REACTIVE(ClientMetrics metrics) {
    SimpleExecutor executor = SimpleExecutor.create("routing-executor");
    AggregatedCacheRouter engine = new AggregatedCacheRouter(executor);
    return new ClientRouter(executor, engine, engine);
  }

  public static ClientRouter FINDER(ClientMetrics metrics, FinderService finder, MachinePicker pickerFallback, String region) {
    SimpleExecutor executor = SimpleExecutor.create("routing-executor");
    AggregatedCacheRouter engine = new AggregatedCacheRouter(executor);
    MachinePicker picker = new ReactiveCacheMachinePicker(metrics, engine, pickerFallback);
    FinderServiceRouter routerForDocuments = new FinderServiceRouter(executor, finder, picker, region);
    return new ClientRouter(executor, engine, routerForDocuments);
  }
}
