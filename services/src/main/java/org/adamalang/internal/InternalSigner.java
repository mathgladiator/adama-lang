/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    return Jwts.builder().setClaims(claims).setIssuer("internal").setSubject(who.agent).signWith(privateKey).compact();
  }
}
