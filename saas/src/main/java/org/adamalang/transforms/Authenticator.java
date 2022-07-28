/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.transforms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.connection.Session;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.model.Authorities;
import org.adamalang.mysql.model.Users;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.Keystore;

import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

public class Authenticator {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(Authenticator.class);
  public final ExternNexus nexus;

  public Authenticator(ExternNexus nexus) {
    this.nexus = nexus;
  }

  public static void logInto(AuthenticatedUser user, ObjectNode node) {
    if (user != null) {
      node.put("user-source", user.source.toString());
      if (user.source == AuthenticatedUser.Source.Adama) {
        node.put("user-id", user.id);
      }
      if (user.who != null) {
        node.put("client-agent", user.who.agent);
        node.put("client-authority", user.who.authority);
      }
    }
  }

  public void execute(Session session, String identity, Callback<AuthenticatedUser> callback) {
    AuthenticatedUser cacheHit = session.identityCache.get(identity);
    if (cacheHit != null) {
      callback.success(cacheHit);
      return;
    }
    try {
      // TODO: check for Google Prefix
      ParsedToken parsedToken = new ParsedToken(identity);
      if ("adama".equals(parsedToken.iss)) {
        int userId = Integer.parseInt(parsedToken.sub);
        for (String publicKey64 : Users.listKeys(nexus.dataBase, userId)) {
          byte[] publicKey = Base64.getDecoder().decode(publicKey64);
          X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
          KeyFactory kf = KeyFactory.getInstance("EC");
          try {
            Jwts.parserBuilder()
                .setSigningKey(kf.generatePublic(spec))
                .requireIssuer("adama")
                .build()
                .parseClaimsJws(identity);
            AuthenticatedUser user = new AuthenticatedUser(AuthenticatedUser.Source.Adama, userId, new NtPrincipal("" + userId, "adama"));
            session.identityCache.put(identity, user);
            callback.success(user);
            return;
          } catch (Exception ex) {
            // move on
          }
        }
        callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FAILED_FINDING_DEVELOPER_KEY));
      } else {
        String keystoreJson = Authorities.getKeystoreInternal(nexus.dataBase, parsedToken.iss);
        Keystore keystore = Keystore.parse(keystoreJson);
        NtPrincipal who = keystore.validate(parsedToken.iss, identity);
        AuthenticatedUser user = new AuthenticatedUser(AuthenticatedUser.Source.Authority, -1, who);
        session.identityCache.put(identity, user);
        callback.success(user);
      }
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.AUTH_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  /** a pre-validated parsed token; we parse to find which keys to look up */
  public static class ParsedToken {
    public final String iss;
    public final String sub;

    public ParsedToken(String token) throws ErrorCodeException {
      String[] parts = token.split(Pattern.quote("."));
      if (parts.length == 3) {
        try {
          String middle = new String(Base64.getDecoder().decode(parts[1]));
          JsonMapper mapper = new JsonMapper();
          JsonNode tree = mapper.readTree(middle);
          if (tree != null && tree.isObject()) {
            JsonNode _iss = tree.get("iss");
            JsonNode _sub = tree.get("sub");
            if (_iss != null && _iss.isTextual() && _sub != null && _sub.isTextual()) {
              this.iss = _iss.textValue();
              this.sub = _sub.textValue();
              return;
            }
          }
          throw new ErrorCodeException(ErrorCodes.AUTH_INVALID_TOKEN_JSON_COMPLETE);
        } catch (Exception ex) {
          throw new ErrorCodeException(ErrorCodes.AUTH_INVALID_TOKEN_JSON, ex);
        }
      } else {
        throw new ErrorCodeException(ErrorCodes.AUTH_INVALID_TOKEN_LAYOUT);
      }
    }
  }
}
