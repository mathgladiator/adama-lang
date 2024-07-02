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
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.HashMap;

public class MockCapacityPlanFetcher implements CapacityPlanFetcher {
  public final HashMap<String, CapacityPlan> plans = new HashMap<>();
  @Override
  public void fetch(String space, Callback<CapacityPlan> callback) {
    CapacityPlan plan = plans.get(space);
    if (plan != null) {
      callback.success(plan);
    } else {
      callback.failure(new ErrorCodeException(111));
    }
  }
}
