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

/** Set the viewer's password to the document; requires their old password. */
public class ConnectionPasswordRequest {
  public final Long connection;
  public final String username;
  public final String password;
  public final String new_password;

  public ConnectionPasswordRequest(final Long connection, final String username, final String password, final String new_password) {
    this.connection = connection;
    this.username = username;
    this.password = password;
    this.new_password = new_password;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<ConnectionPasswordRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      final String username = request.getString("username", true, 458737);
      final String password = request.getString("password", true, 465917);
      final String new_password = request.getString("new_password", true, 466931);
      nexus.executor.execute(new NamedRunnable("connectionpassword-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new ConnectionPasswordRequest(connection, username, password, new_password));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("connectionpassword-error") {
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
