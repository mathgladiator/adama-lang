/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.validators.ValidateToken;
import org.adamalang.web.io.*;

/** The converts and validates a google token into an Adama token. */
public class InitConvertGoogleUserRequest {
  public final String accessToken;

  public InitConvertGoogleUserRequest(final String accessToken) {
    this.accessToken = accessToken;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<InitConvertGoogleUserRequest> callback) {
    try {
      final String accessToken = request.getString("access-token", true, 407544);
      ValidateToken.validate(accessToken);
      nexus.executor.execute(new NamedRunnable("initconvertgoogleuser-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new InitConvertGoogleUserRequest(accessToken));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("initconvertgoogleuser-error") {
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
