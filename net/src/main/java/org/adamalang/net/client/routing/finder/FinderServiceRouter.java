/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.routing.finder;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.contracts.RoutingCallback;
import org.adamalang.net.client.routing.Router;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;

import java.util.concurrent.atomic.AtomicInteger;

public class FinderServiceRouter implements Router {

  private final SimpleExecutor executor;
  private final FinderService finder;
  private final MachinePicker picker;
  private final String region;
  private final AtomicInteger findFailureBackoff;

  public FinderServiceRouter(SimpleExecutor executor, FinderService finder, MachinePicker picker, String region) {
    this.executor = executor;
    this.finder = finder;
    this.picker = picker;
    this.region = region;
    this.findFailureBackoff = new AtomicInteger(1);
  }

  private void reportFindSuccess() {
    this.findFailureBackoff.set(Math.max(1, (int) (findFailureBackoff.get() * Math.random())));
  }

  private int reportFindFailureGetRetryBackoff() {
    int prior = findFailureBackoff.get();
    this.findFailureBackoff.set(Math.min(5000, (int) (prior * (1.0 + Math.random()))) + 1);
    return prior;
  }

  private void retryFailure(ErrorCodeException ex, Key key, RoutingCallback callback) {
    if (ex.code == ErrorCodes.UNIVERSAL_LOOKUP_FAILED) {
      pickHost(key, callback, true);
      return;
    }
    int backoff = reportFindFailureGetRetryBackoff();
    if (backoff > 500) {
      callback.failure(new ErrorCodeException(ErrorCodes.NET_FINDER_GAVE_UP));
      return;
    }
    executor.schedule(new NamedRunnable("simple-find-router-retry") {
      @Override
      public void execute() throws Exception {
        get(key, callback);
      }
    }, backoff);
  }

  private void pickHost(Key key, RoutingCallback callback, boolean retry) {
    picker.pickHost(key, new Callback<>() {
      @Override
      public void success(String newMachine) {
        callback.onMachine(newMachine);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        if (retry) {
          executor.schedule(new NamedRunnable("pick-host-retry") {
            @Override
            public void execute() throws Exception {
              pickHost(key, callback, false);
            }
          }, 50 + reportFindFailureGetRetryBackoff());
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.NET_FINDER_FAILED_PICK_HOST));
        }
      }
    });
  }

  @Override
  public void get(Key key, RoutingCallback callback) {
    // find the key once, or assign capacity to it
    finder.find(key, new Callback<>() {
      @Override
      public void success(FinderService.Result finderResult) {
        reportFindSuccess();
        if (finderResult.location == FinderService.Location.Machine) {
          if (finderResult.region.equals(region)) {
            callback.onMachine(finderResult.machine);
          } else {
            callback.onRegion(finderResult.region);
          }
        } else {
          pickHost(key, callback, true);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        retryFailure(ex, key, callback);
      }
    });
  }
}
