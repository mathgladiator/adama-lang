package org.adamalang.runtime.data.managed;

import org.adamalang.common.Callback;

/** tuple of a runnable action along with a callback. This allows us to perform a unit of work, or abort it */
public class Action {
  public final Runnable action;
  public final Callback<?> callback;

  public Action(Runnable action, Callback<?> callback) {
    this.action = action;
    this.callback = callback;
  }
}
