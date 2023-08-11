/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.transforms.results;

import io.jsonwebtoken.Jwts;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.web.io.ConnectionContext;

import java.security.PrivateKey;
import java.util.TreeMap;

/** a user that has been authenticated */
public class AuthenticatedUser {

  /** if the user is an adama developer, then this is their id */
  public final int id;

  /** the principal of the developer */
  public final NtPrincipal who;

  /** details about the connection */
  public final ConnectionContext context;

  public boolean isAdamaDeveloper;

  public AuthenticatedUser(int id, NtPrincipal who, ConnectionContext context) {
    this.id = id;
    this.who = who;
    this.context = context;
    this.isAdamaDeveloper = "adama".equalsIgnoreCase(who.authority);
  }

  /** convert the user to a token for cross-host transmission over the public interwebs */
  public String asIdentity(int keyId, PrivateKey key) {
    TreeMap<String, Object> claims = new TreeMap<>();
    claims.put("kid", keyId);
    claims.put("puid", id);
    claims.put("pa", who.authority);
    claims.put("po", context.origin);
    claims.put("pip", context.remoteIp);
    claims.put("pak", context.assetKey);
    claims.put("pua", context.userAgent);
    return Jwts.builder().setClaims(claims).setIssuer("host").setSubject(who.agent).signWith(key).compact();
  }
}
