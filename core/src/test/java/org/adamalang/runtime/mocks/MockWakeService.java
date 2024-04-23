package org.adamalang.runtime.mocks;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.cron.WakeService;

import java.util.ArrayList;

public class MockWakeService implements WakeService {
  public final ArrayList<String> alarms;

  public MockWakeService() {
    this.alarms = new ArrayList<>();
  }

  @Override
  public synchronized void wakeIn(Key key, long when, Callback<Void> callback) {
    alarms.add("WAKE:" + key.space + "/" + key.key + "@" + when);
  }
}
