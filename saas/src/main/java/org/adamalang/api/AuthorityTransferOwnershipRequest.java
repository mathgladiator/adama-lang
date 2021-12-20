package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class AuthorityTransferOwnershipRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String email;
  public final Integer userId;

  public AuthorityTransferOwnershipRequest(final String identity, final AuthenticatedUser who, final String email, final Integer userId) {
    this.identity = identity;
    this.who = who;
    this.email = email;
    this.userId = userId;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityTransferOwnershipRequest> callback) {
    try {
      final BulkLatch<AuthorityTransferOwnershipRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String email = request.getString("email", true, 473103);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      _latch.with(() -> new AuthorityTransferOwnershipRequest(identity, who.get(), email, userId.get()));
      nexus.identityService.execute(identity, who);
      nexus.emailService.execute(email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
