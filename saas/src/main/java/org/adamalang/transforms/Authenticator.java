/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.transforms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.frontend.Authorities;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.common.Callback;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.AsyncTransform;

import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

import org.adamalang.common.Json;

public class Authenticator implements AsyncTransform<String, AuthenticatedUser> {
    public final ExternNexus nexus;
    private final ExceptionLogger logger;

    public Authenticator(ExternNexus nexus) {
        this.nexus = nexus;
        this.logger = nexus.makeLogger(Authenticator.class);
    }

    @Override
    public void execute(String identity, Callback<AuthenticatedUser> callback) {
        // TODO: think about caching and an implicit "@" for use the most recently authenticate key
        try {
            // TODO: check for Facebook Prefix
            // TODO: check for Google Prefix
            ParsedToken parsedToken = new ParsedToken(identity);
            if ("adama".equals(parsedToken.iss)) {
                int userId = Integer.parseInt(parsedToken.sub);
                for (String publicKey64 : Users.listKeys(nexus.dataBase, userId)) {
                    byte[] publicKey = Base64.getDecoder().decode(publicKey64);
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
                    KeyFactory kf = KeyFactory.getInstance("EC");
                    try {
                        Jwts.parserBuilder().setSigningKey(kf.generatePublic(spec)).requireIssuer("adama").build().parseClaimsJws(identity);
                        callback.success(new AuthenticatedUser(AuthenticatedUser.Source.Adama, userId, new NtClient("" + userId, "adama")));
                        return;
                    } catch (Exception ex) {
                        // move on
                    }
                }
                callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FAILED_FINDING_DEVELOPER_KEY));
            } else {
                ObjectNode keystore = Json.parseJsonObject(Authorities.getKeystoreInternal(nexus.dataBase, parsedToken.iss));
                // TODO: cache the lookup, parsing, teardown
                // TODO: decode the authority
                // TODO: for each public key, test the given token
                callback.failure(new ErrorCodeException(-1));
            }
        } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.AUTH_UNKNOWN_EXCEPTION, ex, logger));
        }
    }

    /** a pre-validated parsed token; we parse to find which keys to look up */
    public class ParsedToken {
        public final String iss;
        public final String sub;

        public ParsedToken(String token) throws ErrorCodeException {
            String[] parts = token.split(Pattern.quote("."));
            if (parts.length == 3) {
                String middle = new String(Base64.getDecoder().decode(parts[1]));
                JsonMapper mapper = new JsonMapper();
                try {
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
