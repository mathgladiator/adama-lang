/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.transforms.global;

import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.SigningKeyPair;
import org.adamalang.frontend.Session;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Authorities;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.mysql.model.Secrets;
import org.adamalang.mysql.model.Users;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.security.Keystore;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.ConnectionContext;

import java.security.PublicKey;
import java.util.regex.Pattern;

/** the authenticator for the global region */
public class GlobalPerSessionAuthenticator extends PerSessionAuthenticator {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(GlobalPerSessionAuthenticator.class);

  private final DataBase database;
  private final String masterKey;
  private final String[] superKeys;

  public GlobalPerSessionAuthenticator(DataBase database, String masterKey, ConnectionContext defaultContext, String[] superKeys) {
    super(defaultContext);
    this.masterKey = masterKey;
    this.superKeys = superKeys;
    this.database = database;
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
      if (identity.startsWith("anonymous:")) {
        String agent = identity.substring("anonymous:".length());
        callback.success(new AuthenticatedUser(AuthenticatedUser.Source.Anonymous, -1, new NtPrincipal(agent, "anonymous"), defaultContext, false));
        return;
      }

      PerSessionAuthenticator.ParsedToken parsedToken = new PerSessionAuthenticator.ParsedToken(identity);
      if (parsedToken.iss.startsWith("doc/")) {
        try {
          String[] docSpaceKey = parsedToken.iss.split(Pattern.quote("/"));
          SigningKeyPair skp = Secrets.getOrCreateDocumentSigningKey(database, masterKey, docSpaceKey[1], docSpaceKey[2]);
          skp.validateTokenThrows(identity);
          NtPrincipal who = new NtPrincipal(parsedToken.sub, parsedToken.iss);
          AuthenticatedUser user = new AuthenticatedUser(AuthenticatedUser.Source.Document, -1, who, defaultContext, false);
          callback.success(user);
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FAILED_DOC_AUTHENTICATE));
        }
        return;
      }

      if ("host".equals(parsedToken.iss)) {
        PublicKey publicKey = decodePublicKey(Hosts.getHostPublicKey(database, parsedToken.key_id));
        Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer("host")
            .build()
            .parseClaimsJws(identity);
        ConnectionContext context = new ConnectionContext(parsedToken.proxy_origin, parsedToken.proxy_ip, parsedToken.proxy_useragent, parsedToken.proxy_asset_key);
        AuthenticatedUser user = new AuthenticatedUser(parsedToken.proxy_source, parsedToken.proxy_user_id, new NtPrincipal(parsedToken.sub, parsedToken.proxy_authority), context, true);
        session.identityCache.put(identity, user);
        callback.success(user);
        return;
      }

      if ("internal".equals(parsedToken.iss)) {
        PublicKey publicKey = decodePublicKey(Hosts.getHostPublicKey(database, parsedToken.key_id));
        Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer("internal")
            .build()
            .parseClaimsJws(identity);
        ConnectionContext context = new ConnectionContext("::adama", "0.0.0.0", "", null);
        AuthenticatedUser user = new AuthenticatedUser(parsedToken.proxy_source, parsedToken.proxy_user_id, new NtPrincipal(parsedToken.sub, parsedToken.proxy_authority), context, true);
        session.identityCache.put(identity, user);
        callback.success(user);
        return;
      }

      if ("super".equals(parsedToken.iss)) {
        for (String publicKey64 : superKeys) {
          PublicKey publicKey = decodePublicKey(publicKey64);
          try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .requireIssuer("super")
                .build()
                .parseClaimsJws(identity);
            AuthenticatedUser user = new AuthenticatedUser(AuthenticatedUser.Source.Super, 0, new NtPrincipal("super", "super"), defaultContext, false);
            session.identityCache.put(identity, user);
            callback.success(user);
            return;
          } catch (Exception ex) {
            ex.printStackTrace();
            // move on
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FAILED_SUPER_AUTHENTICATE));
        return;
      }
      if ("adama".equals(parsedToken.iss)) {
        int userId = Integer.parseInt(parsedToken.sub);
        for (String publicKey64 : Users.listKeys(database, userId)) {
          PublicKey publicKey = decodePublicKey(publicKey64);
          try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .requireIssuer("adama")
                .build()
                .parseClaimsJws(identity);
            AuthenticatedUser user = new AuthenticatedUser(AuthenticatedUser.Source.Adama, userId, new NtPrincipal("" + userId, "adama"), defaultContext, false);
            session.identityCache.put(identity, user);
            callback.success(user);
            return;
          } catch (Exception ex) {
            // move on
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FAILED_FINDING_DEVELOPER_KEY));
        return;
      }

      // otherwise, try a keystore by the authority presented
      String keystoreJson = Authorities.getKeystoreInternal(database, parsedToken.iss);
      Keystore keystore = Keystore.parse(keystoreJson);
      NtPrincipal who = keystore.validate(parsedToken.iss, identity);
      AuthenticatedUser user = new AuthenticatedUser(AuthenticatedUser.Source.Authority, -1, who, defaultContext, false);
      session.identityCache.put(identity, user);
      callback.success(user);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.AUTH_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

}
