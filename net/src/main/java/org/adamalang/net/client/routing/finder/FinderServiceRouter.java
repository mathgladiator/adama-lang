/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.routing.finder;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.net.client.routing.Router;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;

import java.util.function.Consumer;

public class FinderServiceRouter implements Router {

  private final SimpleExecutor executor;
  private final FinderService finder;
  private final String region;

  public FinderServiceRouter(FinderService finder, String region) {
    this.executor = SimpleExecutor.create("simple-router");
    this.finder = finder;
    this.region = region;
  }

  @Override
  public void get(Key key, RoutingSubscriber callback) {
    // find the key once, or assign capacity to it
    finder.find(key, new Callback<>() {
      @Override
      public void success(FinderService.Result finderResult) {
        if (finderResult.location == FinderService.Location.Machine) {
          if (finderResult.region.equals(region)) {
            callback.onMachine(finderResult.machine);
          } else {
            callback.onRegion(finderResult.region);
          }
        } else {
          // TODO: capacity plan, and find a new host
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        executor.schedule(new NamedRunnable("simple-router-retry") {
          @Override
          public void execute() throws Exception {
            get(key, callback);
          }
        }, 50);
      }
    });
  }

  @Override
  public void subscribe(Key key, RoutingSubscriber subscriber, Consumer<Runnable> onCancel) {
    get(key, subscriber);
  }
}
