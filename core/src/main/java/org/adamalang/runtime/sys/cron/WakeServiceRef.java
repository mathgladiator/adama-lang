package org.adamalang.runtime.sys.cron;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

/** introduce a cyclyc connection between a WakeService and KeyAlarm (i.e. CoreService) */
public class WakeServiceRef implements WakeService {
  private WakeService ref;

  public WakeServiceRef() {
    this.ref = null;
  }

  public void set(WakeService ref) {
    this.ref = ref;
  }

  @Override
  public void wakeIn(Key key, long when, Callback<Void> callback) {
    ref.wakeIn(key, when, callback);
  }
}
