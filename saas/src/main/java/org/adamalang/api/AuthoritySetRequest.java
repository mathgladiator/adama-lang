package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.validators.ValidateKeystore;
import org.adamalang.web.io.*;

/**  */
public class AuthoritySetRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String authority;
  public final ObjectNode keyStore;

  public AuthoritySetRequest(final String identity, final AuthenticatedUser who, final String authority, final ObjectNode keyStore) {
    this.identity = identity;
    this.who = who;
    this.authority = authority;
    this.keyStore = keyStore;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthoritySetRequest> callback) {
    try {
      final BulkLatch<AuthoritySetRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String authority = request.getString("authority", true, 430095);
      final ObjectNode keyStore = request.getObject("key-store", true, 457743);
      ValidateKeystore.validate(keyStore);
      _latch.with(() -> new AuthoritySetRequest(identity, who.get(), authority, keyStore));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
