/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.transforms;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.connection.Session;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.transforms.results.SpacePolicy;

import java.util.concurrent.ConcurrentHashMap;

public class SpacePolicyLocator {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(SpacePolicyLocator.class);
  public final SimpleExecutor executor;
  public final DataBase dataBase;
  public final ConcurrentHashMap<String, SpacePolicy> policies;

  public SpacePolicyLocator(SimpleExecutor executor, ExternNexus nexus) {
    this.executor = executor;
    this.dataBase = nexus.database;
    this.policies = new ConcurrentHashMap<>();
  }

  public static void logInto(SpacePolicy policy, ObjectNode node) {
    node.put("space-id", policy.id);
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
            }, 30000);
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
