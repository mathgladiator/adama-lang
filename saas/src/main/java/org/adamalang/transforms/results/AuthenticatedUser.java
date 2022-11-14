/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.transforms.results;

import io.jsonwebtoken.Jwts;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.web.io.ConnectionContext;

import java.security.PrivateKey;
import java.util.TreeMap;

/** a user that has been authenticated */
public class AuthenticatedUser {
  /** where did the user come from */
  public final Source source;

  /** if the user is an adama developer, then this is their id */
  public final int id;

  /** the principal of the developer */
  public final NtPrincipal who;

  /** details about the connection */
  public final ConnectionContext context;

  /** Was this user internally validated */
  public final boolean internal;

  public AuthenticatedUser(Source source, int id, NtPrincipal who, ConnectionContext context, boolean internal) {
    this.source = source;
    this.id = id;
    this.who = who;
    this.context = context;
    this.internal = internal;
  }

  public enum Source {
    Social, //
    Adama, //
    Anonymous, //
    Authority, //
    Super //
  }

  /** convert the user to a token for cross-host transmission over the public interwebs */
  public String asIdentity(int keyId, PrivateKey key) {
    TreeMap<String, Object> claims = new TreeMap<>();
    claims.put("kid", keyId);
    claims.put("ps", source.toString());
    claims.put("puid", id);
    claims.put("pa", who.authority);
    claims.put("po", context.origin);
    claims.put("pip", context.remoteIp);
    claims.put("pak", context.assetKey);
    claims.put("pua", context.userAgent);
    return Jwts.builder().setClaims(claims).setIssuer("host").setSubject(who.agent).signWith(key).compact();
  }
}
