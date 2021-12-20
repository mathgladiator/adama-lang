package org.adamalang.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.web.io.*;

/**  */
public class WebHookRemoveRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String endpoint;

  public WebHookRemoveRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String endpoint) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.endpoint = endpoint;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<WebHookRemoveRequest> callback) {
    try {
      final BulkLatch<WebHookRemoveRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String endpoint = request.getString("endpoint", true, 322);
      _latch.with(() -> new WebHookRemoveRequest(identity, who.get(), space, policy.get(), endpoint));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
