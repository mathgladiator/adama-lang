package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class AuthorityKeysAddRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String name;
  public final String authority;
  public final String publicKey;
  public final String algorithm;
  public final ObjectNode policy;

  public AuthorityKeysAddRequest(final String identity, final AuthenticatedUser who, final String name, final String authority, final String publicKey, final String algorithm, final ObjectNode policy) {
    this.identity = identity;
    this.who = who;
    this.name = name;
    this.authority = authority;
    this.publicKey = publicKey;
    this.algorithm = algorithm;
    this.policy = policy;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityKeysAddRequest> callback) {
    try {
      final BulkLatch<AuthorityKeysAddRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String name = request.getString("name", true, 453647);
      final String authority = request.getString("authority", true, 430095);
      final String publicKey = request.getString("public-key", true, 405519);
      final String algorithm = request.getString("algorithm", true, 481294);
      final ObjectNode policy = request.getObject("policy", true, 457743);
      _latch.with(() -> new AuthorityKeysAddRequest(identity, who.get(), name, authority, publicKey, algorithm, policy));
      nexus.identityService.execute(identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
