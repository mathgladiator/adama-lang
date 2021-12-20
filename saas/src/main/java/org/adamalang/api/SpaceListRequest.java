package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.web.io.*;

/**  */
public class SpaceListRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String marker;
  public final Integer limit;

  public SpaceListRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String marker, final Integer limit) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.marker = marker;
    this.limit = limit;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceListRequest> callback) {
    try {
      final BulkLatch<SpaceListRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String marker = request.getString("marker", false, 0);
      final Integer limit = request.getInteger("limit", false, 0);
      _latch.with(() -> new SpaceListRequest(identity, who.get(), space, policy.get(), marker, limit));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
