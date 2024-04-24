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
