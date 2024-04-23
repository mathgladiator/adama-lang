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
import java.util.Date;
import java.util.TreeMap;

/** a private key with a public id */
public class PrivateKeyWithId {
  public final int keyId;
  public final PrivateKey privateKey;

  public PrivateKeyWithId(int keyId, PrivateKey privateKey) {
    this.keyId = keyId;
    this.privateKey = privateKey;
  }

  private String sign(String agent, String authority, long expiry, String scopes) {
    TreeMap<String, Object> claims = new TreeMap<>();
    claims.put("kid", keyId);
    if (scopes != null) {
      claims.put("scp", scopes);
    }
    JwtBuilder builder = Jwts.builder().claims(claims).subject(agent);
    if (expiry > 0) {
      builder = builder.expiration(new Date(expiry));
    }
    return builder.issuer(authority).signWith(privateKey).compact();
  }

  public String signDocumentIdentity(String agent, String space, String key, long expiry) {
    return sign(agent, "doc/" + space + "/" + key, expiry, null);
  }

  public String signSocialUser(int userId, long expiry, String scopes) {
    return sign("" + userId, "user", expiry, scopes);
  }

  public String signDeveloper(int userId, long expiry) {
    return sign("" + userId, "adama", expiry, null);
  }
}
