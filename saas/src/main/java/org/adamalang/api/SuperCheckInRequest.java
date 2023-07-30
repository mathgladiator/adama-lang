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

/** The super agent periodically checks in.
  * 
  * This is to bring the external highly secure location into the monitoring system via a sentinel. */
public class SuperCheckInRequest {
  public final String identity;
  public final AuthenticatedUser who;

  public SuperCheckInRequest(final String identity, final AuthenticatedUser who) {
    this.identity = identity;
    this.who = who;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<SuperCheckInRequest> callback) {
    try {
      final BulkLatch<SuperCheckInRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      _latch.with(() -> new SuperCheckInRequest(identity, who.get()));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("supercheckin-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
  }
}
