/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.data.WakeTask;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.cron.KeyAlarm;
import org.adamalang.runtime.sys.cron.WakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/** the wake service for the global service */
public class GlobalWakeService implements WakeService {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalWakeService.class);
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

  public void bootstrap(KeyAlarm alarm, String region, String machine) {
    core.list(region, machine, new Callback<List<WakeTask>>() {
      @Override
      public void success(List<WakeTask> tasks) {
        for (WakeTask task : tasks) {
          alarm.wake(task.key);
          core.delete(task.id, Callback.DONT_CARE_VOID);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOGGER.error("wake-service-bootstrap-issue:" + ex.code);
      }
    });
  }
}
