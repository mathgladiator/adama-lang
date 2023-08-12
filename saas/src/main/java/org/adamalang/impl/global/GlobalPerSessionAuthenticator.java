/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.impl.global;

import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.keys.SigningKeyPair;
import org.adamalang.frontend.Session;
import org.adamalang.impl.common.FastAuth;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Authorities;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.mysql.model.Secrets;
import org.adamalang.mysql.model.Users;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.security.Keystore;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.contracts.data.AuthenticatedUser;
import org.adamalang.web.io.ConnectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.regex.Pattern;

/** the authenticator for the global region */
public class GlobalPerSessionAuthenticator extends PerSessionAuthenticator {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalPerSessionAuthenticator.class);
  private final DataBase database;
  private final String masterKey;
  private final String[] superKeys;

  public GlobalPerSessionAuthenticator(DataBase database, String masterKey, ConnectionContext defaultContext, String[] superKeys) {
    super(defaultContext);
    this.masterKey = masterKey;
    this.superKeys = superKeys;
    this.database = database;
  }

  @Override
  public ConnectionContext getDefaultContext() {
    return defaultContext;
  }

  private void authDocument(String identity, PerSessionAuthenticator.ParsedToken parsedToken, Callback<AuthenticatedUser> callback) {
    final SigningKeyPair skp;
    try {
      String[] docSpaceKey = parsedToken.iss.split(Pattern.quote("/"));
      skp = Secrets.getOrCreateDocumentSigningKey(database, masterKey, docSpaceKey[1], docSpaceKey[2]);
    } catch (Exception ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FAILED_DOC_AUTHENTICATE));
      return;
    }
    skp.validateTokenThrows(identity);
    NtPrincipal who = new NtPrincipal(parsedToken.sub, parsedToken.iss);
    AuthenticatedUser user = new AuthenticatedUser(-1, who, defaultContext);
    callback.success(user);
  }

  private void authHost(Session session, String identity, PerSessionAuthenticator.ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    PublicKey publicKey = PublicKeyCodec.decode(Hosts.getHostPublicKey(database, parsedToken.key_id));
    Jwts.parserBuilder()
        .setSigningKey(publicKey)
        .requireIssuer("host")
        .build()
        .parseClaimsJws(identity);
    ConnectionContext context = new ConnectionContext(parsedToken.proxy_origin, parsedToken.proxy_ip, parsedToken.proxy_useragent, parsedToken.proxy_asset_key);
    AuthenticatedUser user = new AuthenticatedUser(parsedToken.proxy_user_id, new NtPrincipal(parsedToken.sub, parsedToken.proxy_authority), context);
    session.identityCache.put(identity, user);
    callback.success(user);
  }

  private void authInternal(Session session, String identity, PerSessionAuthenticator.ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    PublicKey publicKey = PublicKeyCodec.decode(Hosts.getHostPublicKey(database, parsedToken.key_id));
    Jwts.parserBuilder()
        .setSigningKey(publicKey)
        .requireIssuer("internal")
        .build()
        .parseClaimsJws(identity);
    ConnectionContext context = new ConnectionContext("::adama", "0.0.0.0", "", null);
    AuthenticatedUser user = new AuthenticatedUser(parsedToken.proxy_user_id, new NtPrincipal(parsedToken.sub, parsedToken.proxy_authority), context);
    session.identityCache.put(identity, user);
    callback.success(user);
  }

  private boolean authSuper(Session session, String identity, PerSessionAuthenticator.ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    for (String publicKey64 : superKeys) {
      PublicKey publicKey = PublicKeyCodec.decode(publicKey64);
      try {
        Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer("super")
            .build()
            .parseClaimsJws(identity);
        AuthenticatedUser user = new AuthenticatedUser(0, new NtPrincipal("super", "super"), defaultContext);
        session.identityCache.put(identity, user);
        callback.success(user);
        return true;
      } catch (Exception ex) {
        // skip
      }
    }
    return false;
  }

  private void authAdama(Session session, String identity, PerSessionAuthenticator.ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    int userId = Integer.parseInt(parsedToken.sub);
    for (String publicKey64 : Users.listKeys(database, userId)) {
      PublicKey publicKey = PublicKeyCodec.decode(publicKey64);
      try {
        Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer("adama")
            .build()
            .parseClaimsJws(identity);
        AuthenticatedUser user = new AuthenticatedUser(userId, new NtPrincipal("" + userId, "adama"), defaultContext);
        session.identityCache.put(identity, user);
        callback.success(user);
        return;
      } catch (Exception ex) {
        // move on
      }
    }
  }

  private void authKeystore(Session session, String identity, PerSessionAuthenticator.ParsedToken parsedToken, Callback<AuthenticatedUser> callback) throws Exception {
    // otherwise, try a keystore by the authority presented
    final Keystore keystore;
    try {
      String keystoreJson = Authorities.getKeystoreInternal(database, parsedToken.iss);
      keystore = Keystore.parse(keystoreJson);
    } catch (ErrorCodeException ex) {
      callback.failure(ex);
      return;
    }
    NtPrincipal who = keystore.validate(parsedToken.iss, identity);
    AuthenticatedUser user = new AuthenticatedUser(-1, who, defaultContext);
    session.identityCache.put(identity, user);
    callback.success(user);
  }



  /** authenticate */
  @Override
  public void execute(Session session, String identity, Callback<AuthenticatedUser> callback) {
    AuthenticatedUser cacheHit = session.identityCache.get(identity);
    if (cacheHit != null) {
      // TODO: come up with a cache invalidation scheme
      callback.success(cacheHit);
      return;
    }
    try {
      if (FastAuth.process(identity, callback, defaultContext)) {
        return;
      }

      PerSessionAuthenticator.ParsedToken parsedToken = new PerSessionAuthenticator.ParsedToken(identity);
      if (parsedToken.iss.startsWith("doc/")) {
        authDocument(identity, parsedToken, callback);
        return;
      }
      else if ("host".equals(parsedToken.iss)) { // Proxy mode
        authHost(session, identity, parsedToken, callback);
        return;
      }
      else if ("internal".equals(parsedToken.iss)) { // Document calling another document
        authInternal(session, identity, parsedToken, callback);
        return;
      }
      else if ("super".equals(parsedToken.iss)) { // The superman machine
        if (authSuper(session, identity, parsedToken, callback)) {
          return;
        }
      }
      else if ("adama".equals(parsedToken.iss)) {
        authAdama(session, identity, parsedToken, callback);
      } else {
        authKeystore(session, identity, parsedToken, callback);
      }
    } catch (Exception ex) {
      // TODO: classify errors here
      LOGGER.error("auth-issue-not-known:", ex);
    }
    callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FORBIDDEN));
  }

}
