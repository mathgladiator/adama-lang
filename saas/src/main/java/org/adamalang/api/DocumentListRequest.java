package org.adamalang.api;

import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class DocumentListRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;
  public final Integer limit;

  public DocumentListRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy, final Integer limit) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.limit = limit;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<DocumentListRequest> callback) {
    try {
      final BulkLatch<DocumentListRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final Integer limit = request.getInteger("limit", false, 0);
      _latch.with(() -> new DocumentListRequest(identity, who.get(), space, policy.get(), limit));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
