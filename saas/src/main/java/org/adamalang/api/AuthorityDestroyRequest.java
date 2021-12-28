package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class AuthorityDestroyRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String authority;

  public AuthorityDestroyRequest(final String identity, final AuthenticatedUser who, final String authority) {
    this.identity = identity;
    this.who = who;
    this.authority = authority;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityDestroyRequest> callback) {
    try {
      final BulkLatch<AuthorityDestroyRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String authority = request.getString("authority", true, 430095);
      _latch.with(() -> new AuthorityDestroyRequest(identity, who.get(), authority));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
