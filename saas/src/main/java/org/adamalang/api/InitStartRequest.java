/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

/** This establishes a developer machine via email verification. The expectation is that while the email is being sent, the socket is held open for the developer to complete the operation by providing the generated code.
  * 
  * The generated code is securely randomized and tied to the socket to provide a secure way to validate the email on the other end.
  * 
  * Developer accounts are keyed off of email. */
public class InitStartRequest {
  public final String email;
  public final Integer userId;

  public InitStartRequest(final String email, final Integer userId) {
    this.email = email;
    this.userId = userId;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<InitStartRequest> callback) {
    try {
      final BulkLatch<InitStartRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String email = request.getString("email", true, 473103);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      _latch.with(() -> new InitStartRequest(email, userId.get()));
      nexus.emailService.execute(email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
