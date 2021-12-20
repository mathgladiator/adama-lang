package org.adamalang.api;

import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class SpaceRoleSetRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;
  public final Long email;
  public final String role;

  public SpaceRoleSetRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy, final Long email, final String role) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.email = email;
    this.role = role;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceRoleSetRequest> callback) {
    try {
      final BulkLatch<SpaceRoleSetRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final Long email = request.getLong("email", true, 322);
      final String role = request.getString("role", true, 322);
      _latch.with(() -> new SpaceRoleSetRequest(identity, who.get(), space, policy.get(), email, role));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
