package org.adamalang.mysql.data;

import org.adamalang.runtime.data.Key;

/** a task indicating a document needs a wake */
public class WakeTask {
  public final long id;
  public final Key key;
  public final long wake_time;

  public WakeTask(long id, Key key, long wake_time) {
    this.id = id;
    this.key = key;
    this.wake_time = wake_time;
  }
}
