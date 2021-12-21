package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class SpaceListRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String marker;
  public final Integer limit;

  public SpaceListRequest(final String identity, final AuthenticatedUser who, final String marker, final Integer limit) {
    this.identity = identity;
    this.who = who;
    this.marker = marker;
    this.limit = limit;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceListRequest> callback) {
    try {
      final BulkLatch<SpaceListRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String marker = request.getString("marker", false, 0);
      final Integer limit = request.getInteger("limit", false, 0);
      _latch.with(() -> new SpaceListRequest(identity, who.get(), marker, limit));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
