package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class BillingListRequest {
  public final String identity;
  public final NtClient who;

  public BillingListRequest(final String identity, final NtClient who) {
    this.identity = identity;
    this.who = who;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<BillingListRequest> callback) {
    try {
      final BulkLatch<BillingListRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      _latch.with(() -> new BillingListRequest(identity, who.get()));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
