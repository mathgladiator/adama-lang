/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    return builder.setIssuer("doc/" + space + "/" + key).signWith(privateKey).compact();
  }
}
