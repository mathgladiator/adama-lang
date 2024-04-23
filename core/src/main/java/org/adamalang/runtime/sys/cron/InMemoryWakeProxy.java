package org.adamalang.runtime.sys.cron;

import org.adamalang.common.Callback;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.Key;

import java.util.HashSet;

/** this is the first line of waking up a document that has a cron job and is being put to sleep */
public class InMemoryWakeProxy implements WakeService {
  private final SimpleExecutor executor;
  private final KeyAlarm alarm;
  private final WakeService durable;
  private final HashSet<Key> inflight;

  public InMemoryWakeProxy(SimpleExecutor executor, KeyAlarm alarm, WakeService durable) {
    this.executor = executor;
    this.alarm = alarm;
    this.durable = durable;
    this.inflight = new HashSet<>();
  }

  @Override
  public void wakeIn(Key key, long when, Callback<Void> callback) {
    executor.execute(new NamedRunnable("locked") {
      @Override
      public void execute() throws Exception {
        if (!inflight.contains(key)) {
          executor.schedule(new NamedRunnable("wake") {
            @Override
            public void execute() throws Exception {
              inflight.remove(key);
              alarm.wake(key);
            }
          }, when);
          durable.wakeIn(key, when, callback);
        }
      }
    });
  }
}
