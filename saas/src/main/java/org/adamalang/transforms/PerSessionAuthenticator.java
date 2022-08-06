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
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.connection.Session;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.model.Authorities;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.mysql.model.Users;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.Keystore;
import org.adamalang.web.io.ConnectionContext;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

/** This is a per session Authenticator. This is in 1:1 correspondence to a session/connection */
public class PerSessionAuthenticator {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(PerSessionAuthenticator.class);
  private final ExternNexus nexus;
  private ConnectionContext defaultContext;

  public PerSessionAuthenticator(ExternNexus nexus, ConnectionContext defaultContext) {
    this.nexus = nexus;
    this.defaultContext = defaultContext;
  }

  /** update the default asset key within the default context */
  public void updateAssetKey(String assetKey) {
    this.defaultContext = new ConnectionContext(defaultContext.origin, defaultContext.remoteIp, defaultContext.userAgent, assetKey);
  }

  /** get the asset key for the default context. If the session's connection is a user, then this is the user's asset key. */
  public String assetKey() {
    return defaultContext.assetKey;
  }

  /** log the user details into */
  public static void logInto(AuthenticatedUser user, ObjectNode node) {
    if (user != null) {
      node.put("user-source", user.source.toString());
      if (user.source == AuthenticatedUser.Source.Adama) {
        node.put("user-id", user.id);
      }
      if (user.who != null) {
        node.put("principal-agent", user.who.agent);
        node.put("principal-authority", user.who.authority);
      }
      node.put("user-ip", user.context.remoteIp);
      node.put("user-origin", user.context.origin);
      node.put("user-agent", user.context.userAgent);
    }
  }

  /** Invent a key pair for the host to bind to */
  public static KeyPair inventHostKey() {
    return Keys.keyPairFor(SignatureAlgorithm.ES256);
  }

  /** Encode a public key to store within a database */
  public static String encodePublicKey(KeyPair pair) {
    return new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
  }

  /** Decode a public key stored from within a database */
  public static PublicKey decodePublicKey(String publicKey64) throws Exception {
    byte[] publicKey = Base64.getDecoder().decode(publicKey64);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
    KeyFactory kf = KeyFactory.getInstance("EC");
    return kf.generatePublic(spec);
  }

  /** authenticate */
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
      // TODO: check for Google Prefix
      ParsedToken parsedToken = new ParsedToken(identity);
      if ("web-host".equals(parsedToken.iss)) {
        PublicKey publicKey = decodePublicKey(Hosts.getHostPublicKey(nexus.dataBase, parsedToken.key_id));
        Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer("adama")
            .build()
            .parseClaimsJws(identity);
        ConnectionContext context = new ConnectionContext(parsedToken.proxy_origin, parsedToken.proxy_ip, parsedToken.proxy_useragent, parsedToken.proxy_asset_key);
        AuthenticatedUser user = new AuthenticatedUser(parsedToken.proxy_source, parsedToken.proxy_user_id, new NtPrincipal(parsedToken.sub, parsedToken.proxy_authority), context, true);
        session.identityCache.put(identity, user);
        callback.success(user);
        return;
      }
      if ("adama".equals(parsedToken.iss)) {
        int userId = Integer.parseInt(parsedToken.sub);
        for (String publicKey64 : Users.listKeys(nexus.dataBase, userId)) {
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
      } else {
        String keystoreJson = Authorities.getKeystoreInternal(nexus.dataBase, parsedToken.iss);
        Keystore keystore = Keystore.parse(keystoreJson);
        NtPrincipal who = keystore.validate(parsedToken.iss, identity);
        AuthenticatedUser user = new AuthenticatedUser(AuthenticatedUser.Source.Authority, -1, who, defaultContext, false);
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
    public final int key_id;
    public final AuthenticatedUser.Source proxy_source;
    public final int proxy_user_id;
    public final String proxy_authority;
    public final String proxy_origin;
    public final String proxy_ip;
    public final String proxy_asset_key;
    public final String proxy_useragent;

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
            JsonNode _key_id = tree.get("kid");
            if (_key_id != null && _key_id.isIntegralNumber()) {
              this.key_id = _key_id.asInt();
              this.proxy_source = AuthenticatedUser.Source.valueOf(tree.get("ps").asText());
              this.proxy_user_id = tree.get("puid").asInt();
              this.proxy_authority = tree.get("pa").asText();
              this.proxy_origin = tree.get("po").asText();
              this.proxy_ip = tree.get("pip").asText();
              this.proxy_asset_key = tree.get("pak").asText();
              this.proxy_useragent = tree.get("pua").asText();
            } else {
              this.key_id = -1;
              this.proxy_source = null;
              this.proxy_user_id = 0;
              this.proxy_authority = null;
              this.proxy_origin = null;
              this.proxy_ip = null;
              this.proxy_asset_key = null;
              this.proxy_useragent = null;
            }
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
