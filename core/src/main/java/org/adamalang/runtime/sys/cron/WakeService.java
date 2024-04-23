package org.adamalang.runtime.sys.cron;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

/* the wake up service allows a document to be closed with a reminder to wake it back up */
public interface WakeService {
  /** wake up the document at the given time */
  public void wakeIn(Key key, long when, Callback<Void> callback);
}
