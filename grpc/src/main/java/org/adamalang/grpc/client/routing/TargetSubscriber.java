package org.adamalang.grpc.client.routing;

import java.util.function.Consumer;

/**
 * wraps a callback from the interested party and then dedupes events such that only distinct
 * elements make it through
 */
public class TargetSubscriber {
  private final Consumer<String> callback;
  private String priorTarget;

  public TargetSubscriber(Consumer<String> callback, String target) {
    this.callback = callback;
    this.priorTarget = target;
    callback.accept(target);
  }

  public void set(String newTarget) {
    if (newTarget != null) {
      if (!newTarget.equals(priorTarget)) {
        priorTarget = newTarget;
        callback.accept(newTarget);
      }
    } else if (priorTarget != null) {
      priorTarget = newTarget;
      callback.accept(null);
    }
  }
}
