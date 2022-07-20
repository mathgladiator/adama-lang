/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.routing;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.net.client.contracts.SpaceTrackingEvents;
import org.adamalang.net.client.routing.finder.FinderServiceRouter;
import org.adamalang.net.client.routing.finder.MachinePicker;
import org.adamalang.net.client.routing.reactive.ReativeRoutingEngine;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientRouter {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientRouter.class);
  private final SimpleExecutor executor;
  public final ReativeRoutingEngine engine;
  public final Router routerForDocuments;

  public ClientRouter(SimpleExecutor executor, ReativeRoutingEngine engine, Router routerForDocuments) {
    this.executor = executor;
    this.engine = engine;
    this.routerForDocuments = routerForDocuments;
  }

  public void shutdown() {
    AwaitHelper.block(executor.shutdown(), 1000);
  }

  public static ClientRouter REACTIVE(ClientMetrics metrics) {
    SimpleExecutor executor = SimpleExecutor.create("routing-executor");
    ReativeRoutingEngine engine = new ReativeRoutingEngine(metrics, executor, SpaceTrackingEvents.NoOp, 250, 250);
    return new ClientRouter(executor, engine, engine);
  }

  public static ClientRouter FINDER(ClientMetrics metrics, FinderService finder, String region) {
    SimpleExecutor executor = SimpleExecutor.create("routing-executor");
    ReativeRoutingEngine engine = new ReativeRoutingEngine(metrics, executor, SpaceTrackingEvents.NoOp, 250, 250);
    MachinePicker picker = new MachinePicker() {
      @Override
      public void pickHost(Key key, Callback<String> callback) {
        engine.get(key, new RoutingSubscriber() {
          @Override
          public void onRegion(String region) {
            // impossible for now
            callback.failure(new ErrorCodeException(ErrorCodes.NET_FINDER_ROUTER_REGION_NOT_EXPECTED));
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }

          @Override
          public void onMachine(String machine) {
            if (machine == null) {
              LOGGER.error("failed-find-find-host: {}/{}", key.space, key.key);
              metrics.client_failed_pick_host.run();
              callback.failure(new ErrorCodeException(ErrorCodes.NET_FINDER_ROUTER_NULL_MACHINE));
            } else {
              callback.success(machine);
            }
          }
        });
      }
    };
    FinderServiceRouter routerForDocuments = new FinderServiceRouter(executor, finder, picker, region);
    return new ClientRouter(executor, engine, routerForDocuments);
  }
}
