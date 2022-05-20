package org.adamalang.net.client.routing;

import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.runtime.data.Key;

import java.util.function.Consumer;

public interface Router {
  public void get(Key key, RoutingSubscriber callback);

  public void subscribe(Key key, RoutingSubscriber subscriber, Consumer<Runnable> onCancel);
}
