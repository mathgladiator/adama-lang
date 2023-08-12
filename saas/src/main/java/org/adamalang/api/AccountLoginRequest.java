/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateEmail;
import org.adamalang.web.io.*;

/** Sign an Adama developer in with an email and password pair. */
public class AccountLoginRequest {
  public final String email;
  public final Integer userId;
  public final String password;

  public AccountLoginRequest(final String email, final Integer userId, final String password) {
    this.email = email;
    this.userId = userId;
    this.password = password;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<AccountLoginRequest> callback) {
    try {
      final BulkLatch<AccountLoginRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String email = request.getString("email", true, 473103);
      ValidateEmail.validate(email);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      final String password = request.getString("password", true, 465917);
      _latch.with(() -> new AccountLoginRequest(email, userId.get(), password));
      nexus.emailService.execute(session, email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("accountlogin-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("email", email);
    org.adamalang.contracts.UserIdResolver.logInto(userId, _node);
  }
}
