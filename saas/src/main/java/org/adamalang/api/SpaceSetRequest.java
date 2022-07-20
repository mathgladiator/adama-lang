/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidatePlan;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/**  */
public class SpaceSetRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final ObjectNode plan;

  public SpaceSetRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final ObjectNode plan) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.plan = plan;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceSetRequest> callback) {
    try {
      final BulkLatch<SpaceSetRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final ObjectNode plan = request.getObject("plan", true, 425999);
      ValidatePlan.validate(plan);
      _latch.with(() -> new SpaceSetRequest(identity, who.get(), space, policy.get(), plan));
      nexus.identityService.execute(nexus.session, identity, who);
      nexus.spaceService.execute(nexus.session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("spaceset-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.Authenticator.logInto(who, _node);
    _node.put("space", space);
    org.adamalang.transforms.SpacePolicyLocator.logInto(policy, _node);
  }
}
