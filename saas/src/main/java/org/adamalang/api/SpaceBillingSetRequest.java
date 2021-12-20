package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.web.io.*;

/** Each space is bill seperately. */
public class SpaceBillingSetRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String name;

  public SpaceBillingSetRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String name) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.name = name;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceBillingSetRequest> callback) {
    try {
      final BulkLatch<SpaceBillingSetRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String name = request.getString("name", true, 32423);
      _latch.with(() -> new SpaceBillingSetRequest(identity, who.get(), space, policy.get(), name));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
