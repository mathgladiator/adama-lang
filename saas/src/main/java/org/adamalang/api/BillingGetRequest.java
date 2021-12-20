package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class BillingGetRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String name;

  public BillingGetRequest(final String identity, final AuthenticatedUser who, final String name) {
    this.identity = identity;
    this.who = who;
    this.name = name;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<BillingGetRequest> callback) {
    try {
      final BulkLatch<BillingGetRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String name = request.getString("name", true, 32423);
      _latch.with(() -> new BillingGetRequest(identity, who.get(), name));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
