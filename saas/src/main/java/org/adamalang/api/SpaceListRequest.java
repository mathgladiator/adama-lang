/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/** List the spaces available to the user. */
public class SpaceListRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String marker;
  public final Integer limit;

  public SpaceListRequest(final String identity, final AuthenticatedUser who, final String marker, final Integer limit) {
    this.identity = identity;
    this.who = who;
    this.marker = marker;
    this.limit = limit;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<SpaceListRequest> callback) {
    try {
      final BulkLatch<SpaceListRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String marker = request.getString("marker", false, 0);
      final Integer limit = request.getInteger("limit", false, 0);
      _latch.with(() -> new SpaceListRequest(identity, who.get(), marker, limit));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("spacelist-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("marker", marker);
    _node.put("limit", limit);
  }
}
