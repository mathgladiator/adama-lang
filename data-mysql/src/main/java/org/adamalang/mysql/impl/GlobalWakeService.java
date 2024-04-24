package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.cron.WakeService;

/** the wake service for the global service */
public class GlobalWakeService implements WakeService {
  public final MySQLWakeCore core;
  public final String region;
  public final String machine;

  public GlobalWakeService(MySQLWakeCore core, String region, String machine) {
    this.core = core;
    this.region = region;
    this.machine = machine;
  }

  @Override
  public void wakeIn(Key key, long when, Callback<Void> callback) {
    core.wake(key, when, region, machine, callback);
  }
}
