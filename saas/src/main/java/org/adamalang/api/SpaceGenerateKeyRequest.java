/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Generate a secret key for a space.
  * 
  * First party and third party services require secrets such as api tokens or credentials.
  * 
  * These credentials must be encrypted within the Adama document using a public-private key, and the secret is derived via a key exchange.
  * Here, the server will generate a public/private key pair and store the private key securely and give the developer a public key.
  * The developer then generates a public/private key, encrypts the token with the private key, throws away the private key, and then embeds the key id, the developer's public key, and the encrypted credential within the adama source code. */
public class SpaceGenerateKeyRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;

  public SpaceGenerateKeyRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<SpaceGenerateKeyRequest> callback) {
    try {
      final BulkLatch<SpaceGenerateKeyRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      _latch.with(() -> new SpaceGenerateKeyRequest(identity, who.get(), space, policy.get()));
      nexus.identityService.execute(session, identity, who);
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("spacegeneratekey-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("space", space);
    org.adamalang.transforms.SpacePolicyLocator.logInto(policy, _node);
  }
}
