/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.connection.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/**  */
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

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceSetRoleRequest> callback) {
    try {
      final BulkLatch<SpaceSetRoleRequest> _latch = new BulkLatch<>(nexus.executor, 3, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String email = request.getString("email", true, 473103);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      final String role = request.getString("role", true, 456716);
      _latch.with(() -> new SpaceSetRoleRequest(identity, who.get(), space, policy.get(), email, userId.get(), role));
      nexus.identityService.execute(nexus.session, identity, who);
      nexus.spaceService.execute(nexus.session, space, policy);
      nexus.emailService.execute(nexus.session, email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
