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

/** For regional proxies to lookup domains. */
public class RegionalInitHostRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String region;
  public final String machine;
  public final String role;
  public final String publicKey;

  public RegionalInitHostRequest(final String identity, final AuthenticatedUser who, final String region, final String machine, final String role, final String publicKey) {
    this.identity = identity;
    this.who = who;
    this.region = region;
    this.machine = machine;
    this.role = role;
    this.publicKey = publicKey;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<RegionalInitHostRequest> callback) {
    try {
      final BulkLatch<RegionalInitHostRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String region = request.getString("region", true, 9006);
      final String machine = request.getString("machine", true, 9005);
      final String role = request.getString("role", true, 456716);
      final String publicKey = request.getString("public-key", true, 90000);
      _latch.with(() -> new RegionalInitHostRequest(identity, who.get(), region, machine, role, publicKey));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("regionalinithost-error") {
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
    _node.put("role", role);
  }
}
