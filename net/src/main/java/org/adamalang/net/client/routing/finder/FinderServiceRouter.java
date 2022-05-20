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

  public FinderServiceRouter(FinderService finder) {
    this.executor = SimpleExecutor.create("simple-router");
    this.finder = finder;
  }

  @Override
  public void get(Key key, RoutingSubscriber callback) {
    // find the key once, or assign capacity to it
    finder.find(key, new Callback<>() {
      @Override
      public void success(FinderService.Result finderResult) {
        if (finderResult.location == FinderService.Location.Machine) {
          // TODO: What about region?
          callback.onMachine(finderResult.value);
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
