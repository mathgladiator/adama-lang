/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
      SpaceInfo info = Spaces.getSpaceInfo(database, space);
      String plan = Spaces.getPlan(database, info.id);
      TreeMap<Integer, PrivateKeyBundle> keys = Secrets.getKeys(database, masterKey, space);
      DeploymentPlan deploymentPlan = new DeploymentPlan(plan, LOGGER);
      DeploymentBundle bundle = new DeploymentBundle(deploymentPlan, keys);
      callback.success(bundle);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.PLAN_FETCH_LOOKUP_FAILURE, ex, LOGGER));
    }
  }
}
