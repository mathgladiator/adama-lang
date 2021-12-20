package org.adamalang.api;

import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class SpaceDeleteRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;

  public SpaceDeleteRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceDeleteRequest> callback) {
    try {
      final BulkLatch<SpaceDeleteRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      _latch.with(() -> new SpaceDeleteRequest(identity, who.get(), space, policy.get()));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
