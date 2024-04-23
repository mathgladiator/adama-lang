package org.adamalang.runtime.sys.cron;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

/** some environments or tests don't want to do anything */
public class NoOpWakeService implements WakeService {
  @Override
  public void wakeIn(Key key, long when, Callback<Void> callback) {
    callback.success(null);
  }
}
