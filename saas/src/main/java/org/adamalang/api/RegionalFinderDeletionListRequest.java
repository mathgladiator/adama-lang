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
import org.adamalang.contracts.data.AuthenticatedUser;
import org.adamalang.frontend.Session;
import org.adamalang.web.io.*;

/** List keys that are in the process of being deleted from this host (i.e. marked and not committed) */
public class RegionalFinderDeletionListRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String region;
  public final String machine;

  public RegionalFinderDeletionListRequest(final String identity, final AuthenticatedUser who, final String region, final String machine) {
    this.identity = identity;
    this.who = who;
    this.region = region;
    this.machine = machine;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<RegionalFinderDeletionListRequest> callback) {
    try {
      final BulkLatch<RegionalFinderDeletionListRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String region = request.getString("region", true, 9006);
      final String machine = request.getString("machine", true, 9005);
      _latch.with(() -> new RegionalFinderDeletionListRequest(identity, who.get(), region, machine));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("regionalfinderdeletionlist-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("region", region);
    _node.put("machine", machine);
  }
}
