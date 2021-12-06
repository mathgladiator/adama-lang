package org.adamalang.web.api;

import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.extern.Authenticator;
import org.adamalang.web.extern.SpacePolicy;
import org.adamalang.web.extern.SpacePolicyLocator;
import org.adamalang.web.io.*;

class CreateSpaceRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;

  public CreateSpaceRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<CreateSpaceRequest> callback) {
    try {
      final BulkLatch<CreateSpaceRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 4232);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 2324);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      _latch.with(() -> new CreateSpaceRequest(identity, who.get(), space, policy.get()));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
