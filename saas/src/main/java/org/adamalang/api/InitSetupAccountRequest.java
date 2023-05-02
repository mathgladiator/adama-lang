/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.validators.ValidateEmail;
import org.adamalang.web.io.*;

/** This initiates developer machine via email verification. */
public class InitSetupAccountRequest {
  public final String email;
  public final Integer userId;

  public InitSetupAccountRequest(final String email, final Integer userId) {
    this.email = email;
    this.userId = userId;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<InitSetupAccountRequest> callback) {
    try {
      final BulkLatch<InitSetupAccountRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String email = request.getString("email", true, 473103);
      ValidateEmail.validate(email);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      _latch.with(() -> new InitSetupAccountRequest(email, userId.get()));
      nexus.emailService.execute(session, email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("initsetupaccount-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("email", email);
    org.adamalang.transforms.UserIdResolver.logInto(userId, _node);
  }
}
