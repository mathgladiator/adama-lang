/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
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

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<ConnectionSendRequest> callback) {
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
