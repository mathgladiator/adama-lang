/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.cron.KeyAlarm;

import java.util.ArrayList;

public class MockKeyAlarm implements KeyAlarm {
  public final ArrayList<String> alarms;

  public MockKeyAlarm() {
    this.alarms = new ArrayList<>();
  }

  public String get(int k) {
    return alarms.get(k);
  }

  @Override
  public void wake(Key key) {
    alarms.add("ALARM:" + key.space + "/" + key.key);
  }
}
