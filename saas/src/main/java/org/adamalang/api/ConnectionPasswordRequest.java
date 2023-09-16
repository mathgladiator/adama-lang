/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
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

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<ConnectionPasswordRequest> callback) {
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
