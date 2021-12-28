package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

/**  */
public class ConnectionEndRequest {
  public final Long connection;

  public ConnectionEndRequest(final Long connection) {
    this.connection = connection;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<ConnectionEndRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
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
