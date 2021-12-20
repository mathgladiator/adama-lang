package org.adamalang.transforms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.AsyncTransform;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Authenticator implements AsyncTransform<String, AuthenticatedUser> {

    public final ExternNexus nexus;

    public Authenticator(ExternNexus nexus) {
        this.nexus = nexus;
    }

    @Override
    public void execute(String identity, Callback<AuthenticatedUser> callback) {
        try {
            TokenParts tokenParts = new TokenParts(identity);
            // TODO: check for Facebook Prefix
            // TODO: check for Google Prefix
            if ("adama".equals(tokenParts.iss)) {
                int userId = Integer.parseInt(tokenParts.sub);
                for (String publicKey64 : Users.listKeys(nexus.base, userId)) {
                    byte[] publicKey = Base64.getDecoder().decode(publicKey64);
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    Jwts.parserBuilder().setSigningKey(kf.generatePublic(spec)).requireIssuer("adama").build().parseClaimsJws(identity);
                    callback.success(new AuthenticatedUser(userId, new NtClient("" + userId, "adama")));
                }
            } else {
                // TODO: list all public keys for the authority
            }
        } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(124, ex));
        }
        callback.success(new AuthenticatedUser(0, NtClient.NO_ONE));
    }

    public class TokenParts {
        public final String iss;
        public final String sub;

        public TokenParts(String token) throws Exception {
            String[] parts = token.split(Pattern.quote("."));
            if (parts.length == 3) {
                String middle = new String(Base64.getDecoder().decode(parts[1]));
                JsonMapper mapper = new JsonMapper();
                JsonNode tree = mapper.readTree(middle);
                if (tree != null && tree.isObject()) {
                    JsonNode _iss = ((ObjectNode) tree).get("iss");
                    JsonNode _sub = ((ObjectNode) tree).get("sub");
                    if (_iss != null && _iss.isTextual() && _sub != null && _sub.isTextual()) {
                        this.iss = _iss.textValue();
                        this.sub = _sub.textValue();
                        return;
                    }
                }
            }
            throw new Exception("no authority or invalid format");
        }
    }
}

/**
 *


    public static void main(String[] args) throws Exception {
        // Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);


        KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);

        Map<String, Object> header = new HashMap<>();
        header.put("kid", "23");

        Map<String, Object> body = new HashMap<>();
        body.put("p", "*"); // client can do anything, here we can add policies which are signed

        String token = Jwts.builder().setHeader(header).setClaims(body).setSubject("agent").setIssuer("authority").signWith(pair.getPrivate()).compact();
        String issuer = getAuthority(token);
        System.err.println(token + " -> " + issuer);

        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(pair.getPublic()).requireIssuer(issuer).build().parseClaimsJws(token);
        System.err.println("P? := `" + claims.getBody().get("p") + "`");
        System.err.println(claims.getBody().getSubject());


 */