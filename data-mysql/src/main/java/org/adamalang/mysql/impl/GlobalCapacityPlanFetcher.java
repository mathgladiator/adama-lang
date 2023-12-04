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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.sys.capacity.CapacityPlan;
import org.adamalang.runtime.sys.capacity.CapacityPlanFetcher;

/** global capacity planner impl (talks to db) */
public class GlobalCapacityPlanFetcher implements CapacityPlanFetcher {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(GlobalCapacityPlanFetcher.class);
  private final DataBase dataBase;

  public GlobalCapacityPlanFetcher(DataBase dataBase) {
    this.dataBase = dataBase;
  }

  @Override
  public void fetch(String space, Callback<CapacityPlan> callback) {
    try {
      String raw = Spaces.getCapacity(dataBase, space);
      final CapacityPlan plan;
      if (raw == null) {
        plan = new CapacityPlan(new JsonStreamReader("{}"));
      } else {
        plan = new CapacityPlan(new JsonStreamReader(raw));
      }
      callback.success(plan);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.CAPACITY_FETCH_LOOKUP_FAILURE, ex, LOGGER));
    }
  }
}
