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
import org.adamalang.validators.ValidateEmail;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Set the role of an Adama developer for a particular space.
  * 
  * Spaces can be shared among Adama developers. */
public class SpaceSetRoleRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String email;
  public final Integer userId;
  public final String role;

  public SpaceSetRoleRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String email, final Integer userId, final String role) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.email = email;
    this.userId = userId;
    this.role = role;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<SpaceSetRoleRequest> callback) {
    try {
      final BulkLatch<SpaceSetRoleRequest> _latch = new BulkLatch<>(nexus.executor, 3, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String email = request.getString("email", true, 473103);
      ValidateEmail.validate(email);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      final String role = request.getString("role", true, 456716);
      _latch.with(() -> new SpaceSetRoleRequest(identity, who.get(), space, policy.get(), email, userId.get(), role));
      nexus.identityService.execute(session, identity, who);
      nexus.spaceService.execute(session, space, policy);
      nexus.emailService.execute(session, email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("spacesetrole-error") {
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
    _node.put("email", email);
    org.adamalang.transforms.UserIdResolver.logInto(userId, _node);
    _node.put("role", role);
  }
}
