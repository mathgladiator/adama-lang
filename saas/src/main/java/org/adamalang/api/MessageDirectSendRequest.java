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
import org.adamalang.frontend.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.validators.ValidateChannel;
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Send a message to a document without a connection */
public class MessageDirectSendRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final String key;
  public final ObjectNode viewerState;
  public final String channel;
  public final JsonNode message;

  public MessageDirectSendRequest(final String identity, final AuthenticatedUser who, final String space, final String key, final ObjectNode viewerState, final String channel, final JsonNode message) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.key = key;
    this.viewerState = viewerState;
    this.channel = channel;
    this.message = message;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<MessageDirectSendRequest> callback) {
    try {
      final BulkLatch<MessageDirectSendRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final ObjectNode viewerState = request.getObject("viewer-state", false, 0);
      final String channel = request.getString("channel", true, 454659);
      ValidateChannel.validate(channel);
      final JsonNode message = request.getJsonNode("message", true, 425987);
      _latch.with(() -> new MessageDirectSendRequest(identity, who.get(), space, key, viewerState, channel, message));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("messagedirectsend-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("space", space);
    _node.put("key", key);
    _node.put("channel", channel);
  }
}
