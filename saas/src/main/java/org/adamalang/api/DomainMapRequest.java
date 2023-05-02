/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Map a domain to a space. */
public class DomainMapRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String domain;
  public final String space;
  public final SpacePolicy policy;
  public final String certificate;

  public DomainMapRequest(final String identity, final AuthenticatedUser who, final String domain, final String space, final SpacePolicy policy, final String certificate) {
    this.identity = identity;
    this.who = who;
    this.domain = domain;
    this.space = space;
    this.policy = policy;
    this.certificate = certificate;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<DomainMapRequest> callback) {
    try {
      final BulkLatch<DomainMapRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String domain = request.getString("domain", true, 488444);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String certificate = request.getString("certificate", false, 0);
      _latch.with(() -> new DomainMapRequest(identity, who.get(), domain, space, policy.get(), certificate));
      nexus.identityService.execute(session, identity, who);
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("domainmap-error") {
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
