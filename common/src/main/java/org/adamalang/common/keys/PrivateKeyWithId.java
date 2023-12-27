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
package org.adamalang.common.keys;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

import java.security.PrivateKey;
import java.util.TreeMap;

/** a private key with a public id */
public class PrivateKeyWithId {
  public final int keyId;
  public final PrivateKey privateKey;

  public PrivateKeyWithId(int keyId, PrivateKey privateKey) {
    this.keyId = keyId;
    this.privateKey = privateKey;
  }

  public String signDocumentIdentity(String agent, String space, String key, int expiry) {
    TreeMap<String, Object> claims = new TreeMap<>();
    claims.put("kid", keyId);
    JwtBuilder builder = Jwts.builder().setClaims(claims).setSubject(agent);
    if (expiry > 0) {
      // TODO
    }
    return builder.issuer("doc/" + space + "/" + key).signWith(privateKey).compact();
  }
}
