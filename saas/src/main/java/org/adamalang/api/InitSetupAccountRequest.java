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
import org.adamalang.validators.ValidateEmail;
import org.adamalang.web.io.*;

/** This initiates developer machine via email verification. */
public class InitSetupAccountRequest {
  public final String email;

  public InitSetupAccountRequest(final String email) {
    this.email = email;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<InitSetupAccountRequest> callback) {
    try {
      final String email = request.getString("email", true, 473103);
      ValidateEmail.validate(email);
      nexus.executor.execute(new NamedRunnable("initsetupaccount-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new InitSetupAccountRequest(email));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("initsetupaccount-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("email", email);
  }
}
