package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.web.io.*;

/**  */
public class SpaceSetRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final ObjectNode plan;

  public SpaceSetRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final ObjectNode plan) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.plan = plan;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceSetRequest> callback) {
    try {
      final BulkLatch<SpaceSetRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final ObjectNode plan = request.getObject("plan", true, 425999);
      _latch.with(() -> new SpaceSetRequest(identity, who.get(), space, policy.get(), plan));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
