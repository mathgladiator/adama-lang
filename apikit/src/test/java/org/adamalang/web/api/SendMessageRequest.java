package org.adamalang.web.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

class SendMessageRequest {
  public final Long stream;
  public final String channel;
  public final ObjectNode message;

  public SendMessageRequest(final Long stream, final String channel, final ObjectNode message) {
    this.stream = stream;
    this.channel = channel;
    this.message = message;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SendMessageRequest> callback) {
    try {
      final Long stream = request.getLong("stream", true, 32423);
      final String channel = request.getString("channel", true, 2324);
      final ObjectNode message = request.getObject("message", true, 324);
      nexus.executor.execute(() -> {
        callback.success(new SendMessageRequest(stream, channel, message));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
