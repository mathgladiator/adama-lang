package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class AuthorityTransferOwnershipRequest {
  public final String identity;
  public final NtClient who;
  public final Long email;

  public AuthorityTransferOwnershipRequest(final String identity, final NtClient who, final Long email) {
    this.identity = identity;
    this.who = who;
    this.email = email;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityTransferOwnershipRequest> callback) {
    try {
      final BulkLatch<AuthorityTransferOwnershipRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final Long email = request.getLong("email", true, 322);
      _latch.with(() -> new AuthorityTransferOwnershipRequest(identity, who.get(), email));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
