package org.adamalang.transforms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.AsyncTransform;

import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

public class Authenticator implements AsyncTransform<String, AuthenticatedUser> {

    public final ExternNexus nexus;

    public Authenticator(ExternNexus nexus) {
        this.nexus = nexus;
    }

    @Override
    public void execute(String identity, Callback<AuthenticatedUser> callback) {
        try {
            // TODO: check for Facebook Prefix
            // TODO: check for Google Prefix
            ParsedToken parsedToken = new ParsedToken(identity);
            if ("adama".equals(parsedToken.iss)) {
                int userId = Integer.parseInt(parsedToken.sub);
                for (String publicKey64 : Users.listKeys(nexus.base, userId)) {
                    byte[] publicKey = Base64.getDecoder().decode(publicKey64);
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
                    KeyFactory kf = KeyFactory.getInstance("EC");
                    try {
                        Jwts.parserBuilder().setSigningKey(kf.generatePublic(spec)).requireIssuer("adama").build().parseClaimsJws(identity);
                        callback.success(new AuthenticatedUser(userId, new NtClient("" + userId, "adama")));
                        return;
                    } catch (Exception ex) {
                        // move on
                    }
                }
                callback.failure(new ErrorCodeException(ErrorCodes.AUTH_FAILED_FINDING_DEVELOPER_KEY));
            } else {
                // TODO: decode the authority
                // TODO: look up the authority
                callback.failure(new ErrorCodeException(-1));
            }
        } catch (Exception ex) {
            // TODO: remove this, but we are going to need a logger of sorts
            ex.printStackTrace();
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.AUTH_UNKNOWN_EXCEPTION, ex));
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