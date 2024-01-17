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
package org.adamalang.auth;

import io.jsonwebtoken.Jwts;
import org.adamalang.common.cache.Measurable;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.web.io.ConnectionContext;

import java.security.PrivateKey;
import java.util.TreeMap;

/** a user that has been authenticated */
public class AuthenticatedUser implements Measurable {

  /** if the user is an adama developer, then this is their id */
  public final int id;

  /** the principal of the developer */
  public final NtPrincipal who;

  /** details about the connection */
  public final ConnectionContext context;

  public boolean isAdamaDeveloper;

  private int size;

  public AuthenticatedUser(int id, NtPrincipal who, ConnectionContext context) {
    this.id = id;
    this.who = who;
    this.context = context;
    this.isAdamaDeveloper = "adama".equalsIgnoreCase(who.authority);
    this.size = (int) (64 + who.memory());
  }

  /** convert the user to a token for cross-host transmission over the public interwebs; we do this so the remote region can capture and validate the IP and Origin */
  public String asIdentity(int keyId, PrivateKey key) {
    TreeMap<String, Object> claims = new TreeMap<>();
    claims.put("kid", keyId);
    claims.put("puid", id);
    claims.put("pa", who.authority);
    claims.put("po", context.origin);
    claims.put("pip", context.remoteIp);
    claims.put("pua", context.userAgent);
    return Jwts.builder().claims(claims).issuer("host").subject(who.agent).signWith(key).compact();
  }

  @Override
  public long measure() {
    return size;
  }
}
