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
import org.adamalang.contracts.data.AuthenticatedUser;
import org.adamalang.contracts.data.SpacePolicy;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** For regional proxies to emit metrics for a document */
public class SpaceMetricsRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String prefix;
  public final ObjectNode metricQuery;

  public SpaceMetricsRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String prefix, final ObjectNode metricQuery) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.prefix = prefix;
    this.metricQuery = metricQuery;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<SpaceMetricsRequest> callback) {
    try {
      final BulkLatch<SpaceMetricsRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String prefix = request.getString("prefix", false, 0);
      final ObjectNode metricQuery = request.getObject("metric-query", false, 0);
      _latch.with(() -> new SpaceMetricsRequest(identity, who.get(), space, policy.get(), prefix, metricQuery));
      nexus.identityService.execute(session, identity, who);
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("spacemetrics-error") {
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
    org.adamalang.contracts.SpacePolicyLocator.logInto(policy, _node);
    _node.put("prefix", prefix);
  }
}
