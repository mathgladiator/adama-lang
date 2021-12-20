package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class AuthorityKeysRemoveRequest {
  public final String identity;
  public final NtClient who;
  public final String name;

  public AuthorityKeysRemoveRequest(final String identity, final NtClient who, final String name) {
    this.identity = identity;
    this.who = who;
    this.name = name;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityKeysRemoveRequest> callback) {
    try {
      final BulkLatch<AuthorityKeysRemoveRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String name = request.getString("name", true, 32423);
      _latch.with(() -> new AuthorityKeysRemoveRequest(identity, who.get(), name));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
