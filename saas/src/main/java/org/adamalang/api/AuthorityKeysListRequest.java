package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class AuthorityKeysListRequest {
  public final String identity;
  public final AuthenticatedUser who;

  public AuthorityKeysListRequest(final String identity, final AuthenticatedUser who) {
    this.identity = identity;
    this.who = who;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityKeysListRequest> callback) {
    try {
      final BulkLatch<AuthorityKeysListRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      _latch.with(() -> new AuthorityKeysListRequest(identity, who.get()));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
