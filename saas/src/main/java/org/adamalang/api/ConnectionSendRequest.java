package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

/**  */
public class ConnectionSendRequest {
  public final Long connection;
  public final String channel;
  public final ObjectNode message;

  public ConnectionSendRequest(final Long connection, final String channel, final ObjectNode message) {
    this.connection = connection;
    this.channel = channel;
    this.message = message;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<ConnectionSendRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 32423);
      final String channel = request.getString("channel", true, 2324);
      final ObjectNode message = request.getObject("message", true, 324);
      nexus.executor.execute(() -> {
        callback.success(new ConnectionSendRequest(connection, channel, message));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
