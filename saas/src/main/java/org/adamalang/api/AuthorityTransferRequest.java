package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class AuthorityTransferRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String authority;
  public final String email;
  public final Integer userId;

  public AuthorityTransferRequest(final String identity, final AuthenticatedUser who, final String authority, final String email, final Integer userId) {
    this.identity = identity;
    this.who = who;
    this.authority = authority;
    this.email = email;
    this.userId = userId;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityTransferRequest> callback) {
    try {
      final BulkLatch<AuthorityTransferRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String authority = request.getString("authority", true, 430095);
      final String email = request.getString("email", true, 473103);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      _latch.with(() -> new AuthorityTransferRequest(identity, who.get(), authority, email, userId.get()));
      nexus.identityService.execute(identity, who);
      nexus.emailService.execute(email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
