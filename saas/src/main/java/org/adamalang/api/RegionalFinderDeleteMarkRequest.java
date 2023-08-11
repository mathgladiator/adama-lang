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
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Delete the document from the finder */
public class RegionalFinderDeleteMarkRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String region;
  public final String machine;

  public RegionalFinderDeleteMarkRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String key, final String region, final String machine) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.region = region;
    this.machine = machine;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<RegionalFinderDeleteMarkRequest> callback) {
    try {
      final BulkLatch<RegionalFinderDeleteMarkRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final String region = request.getString("region", true, 9006);
      final String machine = request.getString("machine", true, 9005);
      _latch.with(() -> new RegionalFinderDeleteMarkRequest(identity, who.get(), space, policy.get(), key, region, machine));
      nexus.identityService.execute(session, identity, who);
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("regionalfinderdeletemark-error") {
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
    org.adamalang.transforms.SpacePolicyLocator.logInto(policy, _node);
    _node.put("key", key);
    _node.put("region", region);
    _node.put("machine", machine);
  }
}
