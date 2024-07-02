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
package org.adamalang.impl.global;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.frontend.Session;
import org.adamalang.frontend.global.GlobalExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.contracts.SpacePolicyLocator;
import org.adamalang.contracts.data.SpacePolicy;

import java.util.concurrent.ConcurrentHashMap;

public class GlobalSpacePolicyLocator implements SpacePolicyLocator {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(GlobalSpacePolicyLocator.class);
  public final SimpleExecutor executor;
  public final DataBase dataBase;
  public final ConcurrentHashMap<String, SpacePolicy> policies;

  public GlobalSpacePolicyLocator(SimpleExecutor executor, GlobalExternNexus nexus) {
    this.executor = executor;
    this.dataBase = nexus.database;
    this.policies = new ConcurrentHashMap<>();
  }

  public void execute(Session session, String spaceName, Callback<SpacePolicy> callback) {
    SpacePolicy policy = policies.get(spaceName);
    if (policy != null) {
      callback.success(policy);
      return;
    }
    executor.execute(new NamedRunnable("space-policy-locate") {
      @Override
      public void execute() throws Exception {
        try {
          SpaceInfo space = Spaces.getSpaceInfo(dataBase, spaceName);
          boolean schedule = policies.putIfAbsent(spaceName, new SpacePolicy(space)) == null;
          callback.success(policies.get(spaceName));
          if (schedule) {
            executor.schedule(new NamedRunnable("expire-policy") {
              @Override
              public void execute() throws Exception {
                policies.remove(spaceName);
              }
            }, 60000);
          }
        } catch (Exception ex) {
          callback.failure(
              ErrorCodeException.detectOrWrap(
                  ErrorCodes.SPACE_POLICY_LOCATOR_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }
    });
  }
}
