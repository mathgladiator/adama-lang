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
package org.adamalang.transforms;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.frontend.Session;
import org.adamalang.contracts.data.AuthenticatedUser;
import org.adamalang.web.io.ConnectionContext;

/** This is a per session Authenticator. This is in 1:1 correspondence to a session/connection */
public abstract class PerSessionAuthenticator {
  protected ConnectionContext defaultContext;

  public PerSessionAuthenticator(ConnectionContext defaultContext) {
    this.defaultContext = defaultContext;
  }

  @Deprecated
  public abstract ConnectionContext getDefaultContext();

  /** update the default asset key within the default context */
  @Deprecated
  public void updateAssetKey(String assetKey) {
    this.defaultContext = new ConnectionContext(defaultContext.origin, defaultContext.remoteIp, defaultContext.userAgent, assetKey, null);
  }

  /** get the asset key for the default context. If the session's connection is a user, then this is the user's asset key. */
  @Deprecated
  public String assetKey() {
    return defaultContext.assetKey;
  }

  /** log the user details into */
  public static void logInto(AuthenticatedUser user, ObjectNode node) {
    if (user != null) {
      if ("adama".equals(user.who.agent)) {
        node.put("user-id", user.id);
      }
      if (user.who != null) {
        node.put("principal-agent", user.who.agent);
        node.put("principal-authority", user.who.authority);
      }
      node.put("user-ip", user.context.remoteIp);
      node.put("user-origin", user.context.origin);
      node.put("user-agent", user.context.userAgent);
    }
  }

  public abstract void execute(Session session, String identity, Callback<AuthenticatedUser> callback);

}
