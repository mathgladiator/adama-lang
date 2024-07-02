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
package org.adamalang.mysql.impl;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.mysql.model.Secrets;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.runtime.contracts.PlanFetcher;
import org.adamalang.runtime.deploy.DeploymentBundle;
import org.adamalang.runtime.deploy.DeploymentPlan;

import java.util.TreeMap;

/** fetch a plan from the database */
public class GlobalPlanFetcher implements PlanFetcher {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(GlobalPlanFetcher.class);
  private final DataBase database;
  private final String masterKey;

  public GlobalPlanFetcher(DataBase database, String masterKey) {
    this.database = database;
    this.masterKey = masterKey;
  }

  @Override
  public void find(String space, Callback<DeploymentBundle> callback) {
    try {
      String plan = Spaces.getPlan(database, space);
      TreeMap<Integer, PrivateKeyBundle> keys = Secrets.getKeys(database, masterKey, space);
      DeploymentPlan deploymentPlan = new DeploymentPlan(plan, LOGGER);
      DeploymentBundle bundle = new DeploymentBundle(deploymentPlan, keys);
      callback.success(bundle);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.PLAN_FETCH_LOOKUP_FAILURE, ex, LOGGER));
    }
  }
}
