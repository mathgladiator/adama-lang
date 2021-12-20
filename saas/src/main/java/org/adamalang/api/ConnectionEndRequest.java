package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

/**  */
public class ConnectionEndRequest {
  public final Long connection;

  public ConnectionEndRequest(final Long connection) {
    this.connection = connection;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<ConnectionEndRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 32423);
      nexus.executor.execute(() -> {
        callback.success(new ConnectionEndRequest(connection));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
