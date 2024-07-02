/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.impl.common;

import org.adamalang.common.Callback;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.web.io.ConnectionContext;

/** the ultra fast and internal auth aspects (shared between global and region) */
public class FastAuth {
  public static boolean process(String identity, Callback<AuthenticatedUser> callback, ConnectionContext context) {
    if (identity.startsWith("anonymous:")) {
      String agent = identity.substring("anonymous:".length());
      callback.success(new AuthenticatedUser(-1, new NtPrincipal(agent, "anonymous"), context));
      return true;
    }
    return false;
  }
}
