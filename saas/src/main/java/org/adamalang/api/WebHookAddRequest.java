package org.adamalang.api;

import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class WebHookAddRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;
  public final String endpoint;

  public WebHookAddRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy, final String endpoint) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.endpoint = endpoint;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<WebHookAddRequest> callback) {
    try {
      final BulkLatch<WebHookAddRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String endpoint = request.getString("endpoint", true, 322);
      _latch.with(() -> new WebHookAddRequest(identity, who.get(), space, policy.get(), endpoint));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
