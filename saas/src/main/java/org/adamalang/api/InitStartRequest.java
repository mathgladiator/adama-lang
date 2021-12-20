package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

/** This establishes a developer machine via email verification. The expectation is that while the email is being sent, the socket is held open for the developer to complete the operation by providing the generated code.
  * 
  * The generated code is securely randomized and tied to the socket to provide a secure way to validate the email on the other end.
  * 
  * Developer accounts are keyed off of email. */
public class InitStartRequest {
  public final Long email;

  public InitStartRequest(final Long email) {
    this.email = email;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<InitStartRequest> callback) {
    try {
      final Long email = request.getLong("email", true, 322);
      nexus.executor.execute(() -> {
        callback.success(new InitStartRequest(email));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
