package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

/** When the developer receives an email, this method is invoked to revoke all existing public keys.
  * 
  * This same code, having been validated via email, can then be used to generate a new key pair. */
public class InitRevokeAllRequest {
  public final Long connection;
  public final String code;

  public InitRevokeAllRequest(final Long connection, final String code) {
    this.connection = connection;
    this.code = code;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<InitRevokeAllRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      final String code = request.getString("code", true, 455681);
      nexus.executor.execute(() -> {
        callback.success(new InitRevokeAllRequest(connection, code));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
