/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.keys;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.common.Json;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** a signing pair for authentication (specifically for documents) */
public class SigningKeyPair {
  public final String algo;
  public final PrivateKey privateKey;
  public final PublicKey publicKey;

  public SigningKeyPair(String masterKey, String encryptedPayload) throws Exception {
    String payload = MasterKey.decrypt(masterKey, encryptedPayload);
    ObjectNode node = Json.parseJsonObject(payload);
    this.algo = node.get("algo").textValue();
    byte[] privateKey = Base64.getDecoder().decode(node.get("private").textValue());
    byte[] publicKey = Base64.getDecoder().decode(node.get("public").textValue());
    this.privateKey = KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(privateKey));
    this.publicKey = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(publicKey));
  }

  public String signDocument(String space, String key, String agent) {
    return Jwts.builder().setSubject(agent).setIssuer("doc/" + space + "/" + key).signWith(privateKey).compact();
  }

  public void validateTokenThrows(String token) {
    Jwts.parserBuilder()
        .setSigningKey(publicKey)
        .build()
        .parseClaimsJws(token);
  }

  public static String generate(String masterKey) throws Exception {
    KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
    ObjectNode localKeyFile = Json.newJsonObject();
    localKeyFile.put("algo", "ES256");
    localKeyFile.put("private", new String(Base64.getEncoder().encode(pair.getPrivate().getEncoded())));
    localKeyFile.put("public", new String(Base64.getEncoder().encode(pair.getPublic().getEncoded())));
    return MasterKey.encrypt(masterKey, localKeyFile.toString());
  }
}
