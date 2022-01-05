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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Validators;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.frontend.Spaces;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.web.io.AsyncTransform;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class SpacePolicyLocator implements AsyncTransform<String, SpacePolicy> {
  public final Executor executor;
  public final DataBase dataBase;
  public final ConcurrentHashMap<String, SpacePolicy> policies;
  private final ExceptionLogger logger;

  public SpacePolicyLocator(Executor executor, ExternNexus nexus) {
    this.executor = executor;
    this.dataBase = nexus.dataBaseManagement;
    this.policies = new ConcurrentHashMap<>();
    this.logger = nexus.makeLogger(SpacePolicyLocator.class);
  }

  @Override
  public void execute(String spaceName, Callback<SpacePolicy> callback) {
    if (!Validators.simple(spaceName, 127)) {
      callback.failure(new ErrorCodeException(ErrorCodes.API_SPACE_INVALID_NAME_FOR_LOOKUP));
      return;
    }
    SpacePolicy policy = policies.get(spaceName);
    if (policy != null) {
      callback.success(policy);
      return;
    }
    executor.execute(
        () -> {
          try {
            Spaces.Space space = Spaces.getSpaceId(dataBase, spaceName);
            policies.putIfAbsent(spaceName, new SpacePolicy(space));
            callback.success(policies.get(spaceName));
          } catch (Exception ex) {
            callback.failure(
                ErrorCodeException.detectOrWrap(
                    ErrorCodes.SPACE_POLICY_LOCATOR_UNKNOWN_EXCEPTION, ex, logger));
          }
        });
  }
}
