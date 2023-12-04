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
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.cache.Measurable;
import org.adamalang.runtime.json.JsonStreamReader;

/** groundwork for specialized capacity settings */
public class CapacityPlan implements Measurable {
  public final int minimum;

  public CapacityPlan(JsonStreamReader reader) {
    int _minimum = 3;
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "min":
            _minimum = reader.readInteger();
            break;
          default:
            reader.skipValue();
        }
      }
    }
    this.minimum = _minimum;
  }

  @Override
  public long measure() {
    return 32;
  }
}
