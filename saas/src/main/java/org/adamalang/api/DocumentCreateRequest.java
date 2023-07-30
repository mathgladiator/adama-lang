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
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Create a document.
  * 
  * The entropy allows the randomization of the document to be fixed at construction time. */
public class DocumentCreateRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final String key;
  public final String entropy;
  public final ObjectNode arg;

  public DocumentCreateRequest(final String identity, final AuthenticatedUser who, final String space, final String key, final String entropy, final ObjectNode arg) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.key = key;
    this.entropy = entropy;
    this.arg = arg;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<DocumentCreateRequest> callback) {
    try {
      final BulkLatch<DocumentCreateRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final String entropy = request.getString("entropy", false, 0);
      final ObjectNode arg = request.getObject("arg", true, 461826);
      _latch.with(() -> new DocumentCreateRequest(identity, who.get(), space, key, entropy, arg));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("documentcreate-error") {
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
    _node.put("key", key);
    _node.put("entropy", entropy);
  }
}
