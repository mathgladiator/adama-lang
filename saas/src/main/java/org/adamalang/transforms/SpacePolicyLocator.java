/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.transforms;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.connection.Session;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.mysql.frontend.data.SpaceInfo;
import org.adamalang.transforms.results.SpacePolicy;

import java.util.concurrent.ConcurrentHashMap;

public class SpacePolicyLocator {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(SpacePolicyLocator.class);
  public final SimpleExecutor executor;
  public final DataBase dataBase;
  public final ConcurrentHashMap<String, SpacePolicy> policies;

  public SpacePolicyLocator(SimpleExecutor executor, ExternNexus nexus) {
    this.executor = executor;
    this.dataBase = nexus.dataBaseManagement;
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
          policies.putIfAbsent(spaceName, new SpacePolicy(space));
          callback.success(policies.get(spaceName));
        } catch (Exception ex) {
          callback.failure(
              ErrorCodeException.detectOrWrap(
                  ErrorCodes.SPACE_POLICY_LOCATOR_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }
    });
  }
}
