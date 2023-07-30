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

/** Destroy an authority.
  * 
  * This is exceptionally dangerous as it will break authentication for any users that have tokens based on that authority. */
public class AuthorityDestroyRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String authority;

  public AuthorityDestroyRequest(final String identity, final AuthenticatedUser who, final String authority) {
    this.identity = identity;
    this.who = who;
    this.authority = authority;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<AuthorityDestroyRequest> callback) {
    try {
      final BulkLatch<AuthorityDestroyRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String authority = request.getString("authority", true, 430095);
      _latch.with(() -> new AuthorityDestroyRequest(identity, who.get(), authority));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("authoritydestroy-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("authority", authority);
  }
}
