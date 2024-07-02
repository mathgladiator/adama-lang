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
package org.adamalang.internal;

import io.jsonwebtoken.Jwts;
import org.adamalang.runtime.natives.NtPrincipal;

import java.security.PrivateKey;
import java.util.TreeMap;

/** sign via an internal private key */
public class InternalSigner {
  private final int keyId;
  private final PrivateKey privateKey;

  public InternalSigner(int keyId, PrivateKey privateKey) {
    this.keyId = keyId;
    this.privateKey = privateKey;
  }

  public String toIdentity(NtPrincipal who) {
    TreeMap<String, Object> claims = new TreeMap<>();
    claims.put("kid", keyId);
    if ("adama".equals(who.authority)) {
      claims.put("puid", Integer.parseInt(who.agent));
    }
    claims.put("pa", who.authority);
    return Jwts.builder().claims(claims).issuer("internal").subject(who.agent).signWith(privateKey).compact();
  }
}
