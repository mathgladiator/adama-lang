package org.adamalang.web.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.concurrent.Executor;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.extern.Authenticator;
import org.adamalang.web.extern.SpacePolicy;
import org.adamalang.web.extern.SpacePolicyLocator;
import org.adamalang.web.io.*;

class CreateDocumentRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String entropy;
  public final ObjectNode arg;

  public CreateDocumentRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy, final String key, final String entropy, final ObjectNode arg) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.entropy = entropy;
    this.arg = arg;
  }

  public static void resolve(Executor executor, Nexus nexus, JsonRequest request, Callback<CreateDocumentRequest> callback) {
    try {
      final BulkLatch<CreateDocumentRequest> _latch = new BulkLatch<>(executor, 2, callback);
      final String identity = request.getString("identity", true, 4232);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 2324);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 2324);
      final String entropy = request.getString("entropy", false, 0);
      final ObjectNode arg = request.getObject("arg", true, 2324);
      _latch.with(() -> new CreateDocumentRequest(identity, who.get(), space, policy.get(), key, entropy, arg));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
