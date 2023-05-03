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
import org.adamalang.web.io.*;

/** For documents that want to hold secrets, then these secrets should not be stored plaintext.
  * 
  * This method provides the client the ability to hash a password for plain text transmission. */
public class DocumentsHashPasswordRequest {
  public final String password;

  public DocumentsHashPasswordRequest(final String password) {
    this.password = password;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<DocumentsHashPasswordRequest> callback) {
    try {
      final String password = request.getString("password", true, 465917);
      nexus.executor.execute(new NamedRunnable("documentshashpassword-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new DocumentsHashPasswordRequest(password));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("documentshashpassword-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
  }
}
