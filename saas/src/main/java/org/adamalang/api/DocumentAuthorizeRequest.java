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
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Authorize a username and password against a document. */
public class DocumentAuthorizeRequest {
  public final String space;
  public final String key;
  public final String username;
  public final String password;

  public DocumentAuthorizeRequest(final String space, final String key, final String username, final String password) {
    this.space = space;
    this.key = key;
    this.username = username;
    this.password = password;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<DocumentAuthorizeRequest> callback) {
    try {
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final String username = request.getString("username", true, 458737);
      final String password = request.getString("password", true, 465917);
      nexus.executor.execute(new NamedRunnable("documentauthorize-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new DocumentAuthorizeRequest(space, key, username, password));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("documentauthorize-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("space", space);
    _node.put("key", key);
  }
}
