package org.adamalang.net.client.routing;

import org.adamalang.runtime.data.Key;

import java.util.function.Consumer;

public interface Router {
  // public void get(Key key, Consumer<String> callback);

  public void subscribe(Key key, Consumer<String> subscriber, Consumer<Runnable> onCancel);
}
