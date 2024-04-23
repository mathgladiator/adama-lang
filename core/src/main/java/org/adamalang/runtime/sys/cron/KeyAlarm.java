package org.adamalang.runtime.sys.cron;

import org.adamalang.runtime.data.Key;

/** contract to wake a document up from slumber */
public interface KeyAlarm {

  /** wakey-wakey */
  public void wake(Key key);
}
