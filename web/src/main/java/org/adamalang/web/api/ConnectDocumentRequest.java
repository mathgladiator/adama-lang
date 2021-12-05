package org.adamalang.web.api;

import java.util.concurrent.Executor;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.extern.Authenticator;
import org.adamalang.web.extern.SpacePolicy;
import org.adamalang.web.extern.SpacePolicyLocator;
import org.adamalang.web.io.*;

class ConnectDocumentRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;

  public ConnectDocumentRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy, final String key) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
  }

  public static void resolve(Executor executor, Nexus nexus, JsonRequest request, Callback<ConnectDocumentRequest> callback) {
    try {
      final BulkLatch<ConnectDocumentRequest> _latch = new BulkLatch<>(executor, 2, callback);
      final String identity = request.getString("identity", true, 4232);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 2324);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 2324);
      _latch.with(() -> new ConnectDocumentRequest(identity, who.get(), space, policy.get(), key));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
