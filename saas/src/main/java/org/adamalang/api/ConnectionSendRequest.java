/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.validators.ValidateChannel;
import org.adamalang.web.io.*;

/** Send a message to the document on the given channel. */
public class ConnectionSendRequest {
  public final Long connection;
  public final String channel;
  public final JsonNode message;

  public ConnectionSendRequest(final Long connection, final String channel, final JsonNode message) {
    this.connection = connection;
    this.channel = channel;
    this.message = message;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<ConnectionSendRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      final String channel = request.getString("channel", true, 454659);
      ValidateChannel.validate(channel);
      final JsonNode message = request.getJsonNode("message", true, 425987);
      nexus.executor.execute(new NamedRunnable("connectionsend-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new ConnectionSendRequest(connection, channel, message));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("connectionsend-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("channel", channel);
  }
}
