package org.adamalang.api;

import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class AttachmentStartRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String filename;
  public final String contentType;

  public AttachmentStartRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy, final String key, final String filename, final String contentType) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.filename = filename;
    this.contentType = contentType;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AttachmentStartRequest> callback) {
    try {
      final BulkLatch<AttachmentStartRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      final String filename = request.getString("filename", true, 322);
      final String contentType = request.getString("content-type", true, 322);
      _latch.with(() -> new AttachmentStartRequest(identity, who.get(), space, policy.get(), key, filename, contentType));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
