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
