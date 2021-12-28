package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class SpaceCreateRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;

  public SpaceCreateRequest(final String identity, final AuthenticatedUser who, final String space) {
    this.identity = identity;
    this.who = who;
    this.space = space;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceCreateRequest> callback) {
    try {
      final BulkLatch<SpaceCreateRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      _latch.with(() -> new SpaceCreateRequest(identity, who.get(), space));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
