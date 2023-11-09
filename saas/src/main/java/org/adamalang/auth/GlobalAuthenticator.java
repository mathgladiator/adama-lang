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
package org.adamalang.auth;

import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.keys.SigningKeyPair;
import org.adamalang.contracts.data.ParsedToken;
import org.adamalang.impl.common.FastAuth;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Authorities;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.mysql.model.Secrets;
import org.adamalang.mysql.model.Users;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.security.Keystore;
import org.adamalang.web.io.ConnectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.regex.Pattern;

public class GlobalAuthenticator implements Authenticator {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalAuthenticator.class);
  private final DataBase database;
  private final String masterKey;
  private final SimpleExecutor executor;

  public GlobalAuthenticator(DataBase database, String masterKey, SimpleExecutor executor) {
    this.masterKey = masterKey;
    this.database = database;
    this.executor = executor;
  }

  private void authDocument(String identity, ParsedToken parsedToken, ConnectionContext context, Callback<AuthenticatedUser> callback) {
    final Runnable auth;
    try {
      if (parsedToken.key_id > 0) {
        PublicKey publicKey = PublicKeyCodec.decode(Hosts.getHostPublicKey(database, parsedToken.key_id));
        auth = () -> Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(identity);
      } else {
        String[] docSpaceKey = parsedToken.iss.split(Pattern.quote("/"));
        SigningKeyPair skp = Secrets.getOrCreateDocumentSigningKey(database, masterKey, docSpaceKey[1], docSpaceKey[2]);
        auth = () -> skp.validateTokenThrows(identity);
      }
    } catch (Exception ex) {
      callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FAILED_DOC_AUTHENTICATE));
      return;
    }
    auth.run();
    NtPrincipal who = new NtPrincipal(parsedToken.sub, parsedToken.iss);
    AuthenticatedUser user = new AuthenticatedUser(-1, who, context);
    callback.success(user);
  }

  private boolean authAdama(String identity, ParsedToken parsedToken, ConnectionContext context, Callback<AuthenticatedUser> callback) throws Exception {
    int userId = Integer.parseInt(parsedToken.sub);
    for (String publicKey64 : Users.listKeys(database, userId)) {
      PublicKey publicKey = PublicKeyCodec.decode(publicKey64);
      try {
        Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer("adama")
            .build()
            .parseClaimsJws(identity);
        AuthenticatedUser user = new AuthenticatedUser(userId, new NtPrincipal("" + userId, "adama"), context);
        callback.success(user);
        return true;
      } catch (Exception ex) {
        // move on
      }
    }
    return false;
  }

  private void authKeystore(String identity, ParsedToken parsedToken, ConnectionContext context, Callback<AuthenticatedUser> callback) throws Exception {
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
    AuthenticatedUser user = new AuthenticatedUser(-1, who, context);
    callback.success(user);
  }

  @Override
  public void auth(AuthRequest request, Callback<AuthenticatedUser> callback) {
    executor.execute(new NamedRunnable("global-auth") {
      @Override
      public void execute() throws Exception {
        try {
          if (FastAuth.process(request.identity, callback, request.context)) {
            return;
          }
          ParsedToken parsedToken = new ParsedToken(request.identity);
          if (parsedToken.iss.startsWith("doc/")) {
            authDocument(request.identity, parsedToken, request.context, callback);
            return;
          } else if ("adama".equals(parsedToken.iss)) {
            if (authAdama(request.identity, parsedToken, request.context, callback)) {
              return;
            }
          } else {
            authKeystore(request.identity, parsedToken, request.context, callback);
            return;
          }
        } catch (Exception ex) {
          LOGGER.error("auth-issue-not-known:", ex);
        }
        callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FORBIDDEN));
      }
    });
  }
}
