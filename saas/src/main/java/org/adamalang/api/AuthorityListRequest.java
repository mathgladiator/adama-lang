package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class AuthorityListRequest {
  public final String identity;
  public final NtClient who;

  public AuthorityListRequest(final String identity, final NtClient who) {
    this.identity = identity;
    this.who = who;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityListRequest> callback) {
    try {
      final BulkLatch<AuthorityListRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      _latch.with(() -> new AuthorityListRequest(identity, who.get()));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
