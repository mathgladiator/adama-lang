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

/** This establishes a developer machine via email verification.
  * 
  * Copy the code from the email into this request.
  * 
  * The server will generate a key-pair and send the secret to the client to stash within their config, and the
  * public key will be stored to validate future requests made by this developer machine.
  * 
  * A public key will be held onto for 30 days. */
public class InitCompleteAccountRequest {
  public final String email;
  public final Integer userId;
  public final Boolean revoke;
  public final String code;

  public InitCompleteAccountRequest(final String email, final Integer userId, final Boolean revoke, final String code) {
    this.email = email;
    this.userId = userId;
    this.revoke = revoke;
    this.code = code;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<InitCompleteAccountRequest> callback) {
    try {
      final BulkLatch<InitCompleteAccountRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String email = request.getString("email", true, 473103);
      ValidateEmail.validate(email);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      final Boolean revoke = request.getBoolean("revoke", false, 0);
      final String code = request.getString("code", true, 455681);
      _latch.with(() -> new InitCompleteAccountRequest(email, userId.get(), revoke, code));
      nexus.emailService.execute(session, email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("initcompleteaccount-error") {
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
    _node.put("revoke", revoke);
  }
}
