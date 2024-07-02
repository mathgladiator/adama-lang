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
package org.adamalang.frontend;

import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.auth.AuthenticatedUser;

import java.util.HashMap;

public class Session {
  public final long created;
  private long lastActivity;
  public HashMap<String, AuthenticatedUser> identityCache;
  public final PerSessionAuthenticator authenticator;

  public Session(final PerSessionAuthenticator authenticator) {
    this.created = System.currentTimeMillis();
    this.identityCache = new HashMap<>();
    this.authenticator = authenticator;
  }

  public synchronized void activity() {
    lastActivity = System.currentTimeMillis();
  }

  public synchronized boolean keepalive() {
    long now = System.currentTimeMillis();
    long timeSinceCreation = now - created;
    long timeSinceLastActivity = now - lastActivity;
    return (timeSinceCreation <= 5 * 60000) || (timeSinceLastActivity <= 2 * 60 * 60000);
  }
}
