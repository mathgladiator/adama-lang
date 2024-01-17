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
package org.adamalang.impl.global;

import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.auth.AuthRequest;
import org.adamalang.auth.Authenticator;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.frontend.Session;
import org.adamalang.impl.common.FastAuth;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.contracts.data.ParsedToken;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.web.io.ConnectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;

/** the authenticator for the global region */
public class GlobalPerSessionAuthenticator extends PerSessionAuthenticator {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalPerSessionAuthenticator.class);
  private final DataBase database;
  private final Authenticator authenticator;
  private final String[] superKeys;
  private final String[] regionalPublicKeys;

  public GlobalPerSessionAuthenticator(DataBase database, Authenticator authenticator, ConnectionContext defaultContext, String[] superKeys, String[] regionalPublicKeys) {
    super(defaultContext);
    this.database = database;
    this.authenticator = authenticator;
    this.superKeys = superKeys;
    this.regionalPublicKeys = regionalPublicKeys;
  }

  @Override
  public ConnectionContext getTransportContext() {
    return transportContext;
  }

  private void authHost(Session session, String identity, ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    PublicKey publicKey = PublicKeyCodec.decode(Hosts.getHostPublicKey(database, parsedToken.key_id));
    Jwts.parser()
        .verifyWith(publicKey)
        .requireIssuer("host")
        .build()
        .parseSignedClaims(identity);
    ConnectionContext context = new ConnectionContext(parsedToken.proxy_origin, parsedToken.proxy_ip, parsedToken.proxy_useragent, null);
    AuthenticatedUser user = new AuthenticatedUser(parsedToken.proxy_user_id, new NtPrincipal(parsedToken.sub, parsedToken.proxy_authority), context);
    session.identityCache.put(identity, user);
    callback.success(user);
  }

  private void authInternal(Session session, String identity, ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    PublicKey publicKey = PublicKeyCodec.decode(Hosts.getHostPublicKey(database, parsedToken.key_id));
    Jwts.parser()
        .verifyWith(publicKey)
        .requireIssuer("internal")
        .build()
        .parseSignedClaims(identity);
    ConnectionContext context = new ConnectionContext("::adama", "0.0.0.0", "", null);
    AuthenticatedUser user = new AuthenticatedUser(parsedToken.proxy_user_id, new NtPrincipal(parsedToken.sub, parsedToken.proxy_authority), context);
    session.identityCache.put(identity, user);
    callback.success(user);
  }

  private boolean authSuper(Session session, String identity, ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    for (String publicKey64 : superKeys) {
      PublicKey publicKey = PublicKeyCodec.decode(publicKey64);
      try {
        Jwts.parser()
            .verifyWith(publicKey)
            .requireIssuer("super")
            .build()
            .parseSignedClaims(identity);
        AuthenticatedUser user = new AuthenticatedUser(0, new NtPrincipal("super", "super"), transportContext);
        session.identityCache.put(identity, user);
        callback.success(user);
        return true;
      } catch (Exception ex) {
        // skip
      }
    }
    return false;
  }

  private boolean authRegion(Session session, String identity, ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    for (String publicKey64 : regionalPublicKeys) {
      PublicKey publicKey = PublicKeyCodec.decode(publicKey64);
      try {
        Jwts.parser()
            .verifyWith(publicKey)
            .requireIssuer("region")
            .build()
            .parseSignedClaims(identity);
        AuthenticatedUser user = new AuthenticatedUser(0, new NtPrincipal(parsedToken.sub, "region"), transportContext);
        session.identityCache.put(identity, user);
        callback.success(user);
        return true;
      } catch (Exception ex) {
        // skip
      }
    }
    return false;
  }

  /** authenticate */
  @Override
  public void execute(Session session, String identityRaw, Callback<AuthenticatedUser> callback) {
    // check to see if there is a cookie to lookup
    String identity = transportContext.identityOf(identityRaw);

    AuthenticatedUser cacheHit = session.identityCache.get(identity);
    if (cacheHit != null) {
      // TODO: come up with a cache invalidation scheme
      callback.success(cacheHit);
      return;
    }
    try {
      if (FastAuth.process(identity, callback, transportContext)) {
        return;
      }
      ParsedToken parsedToken = new ParsedToken(identity);
      if ("host".equals(parsedToken.iss)) { // Proxy mode
        authHost(session, identity, parsedToken, callback);
        return;
      } else if ("internal".equals(parsedToken.iss)) { // Document calling another document
        authInternal(session, identity, parsedToken, callback);
        return;
      } else if ("super".equals(parsedToken.iss)) { // The superman machine
        if (authSuper(session, identity, parsedToken, callback)) {
          return;
        }
      } else if ("region".equals(parsedToken.iss)) { // The superman machine
        if (authRegion(session, identity, parsedToken, callback)) {
          return;
        }
      } else {
        authenticator.auth(new AuthRequest(identity, transportContext), callback);
        return;
      }
    } catch (Exception ex) {
      // TODO: classify errors here
      LOGGER.error("auth-issue-not-known:", ex);
    }
    callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FORBIDDEN));
  }
}
