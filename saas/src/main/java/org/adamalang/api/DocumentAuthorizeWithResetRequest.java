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
import org.adamalang.contracts.data.SpacePolicy;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Authorize a username and password against a document, and set a new password */
public class DocumentAuthorizeWithResetRequest {
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String username;
  public final String password;
  public final String new_password;

  public DocumentAuthorizeWithResetRequest(final String space, final SpacePolicy policy, final String key, final String username, final String password, final String new_password) {
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.username = username;
    this.password = password;
    this.new_password = new_password;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<DocumentAuthorizeWithResetRequest> callback) {
    try {
      final BulkLatch<DocumentAuthorizeWithResetRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final String username = request.getString("username", true, 458737);
      final String password = request.getString("password", true, 465917);
      final String new_password = request.getString("new_password", true, 466931);
      _latch.with(() -> new DocumentAuthorizeWithResetRequest(space, policy.get(), key, username, password, new_password));
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("documentauthorizewithreset-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("space", space);
    org.adamalang.contracts.SpacePolicyLocator.logInto(policy, _node);
    _node.put("key", key);
  }
}
