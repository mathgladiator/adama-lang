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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.validators.ValidateChannel;
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
      final Long connection = request.getLong("connection", true, 405505);
      final String channel = request.getString("channel", true, 454659);
      ValidateChannel.validate(channel);
      final ObjectNode message = request.getObject("message", true, 425987);
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
