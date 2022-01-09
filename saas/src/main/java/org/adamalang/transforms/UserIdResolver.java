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
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.web.io.AsyncTransform;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.concurrent.Executor;

public class UserIdResolver implements AsyncTransform<String, Integer> {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(UserIdResolver.class);
  private final Executor executor;
  private final DataBase dataBase;

  public UserIdResolver(Executor executor, ExternNexus nexus) {
    this.executor = executor;
    this.dataBase = nexus.dataBaseManagement;
  }

  @Override
  public void execute(String email, Callback<Integer> callback) {
    executor.execute(
        () -> {
          try {
            if (EmailValidator.getInstance().isValid(email)) {
              callback.success(Users.getOrCreateUserId(dataBase, email));
            } else {
              callback.failure(new ErrorCodeException(ErrorCodes.USERID_RESOLVE_INVALID_EMAIL));
            }
          } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.USERID_RESOLVE_UNKNOWN_EXCEPTION, ex, LOGGER));
          }
        });
  }
}
