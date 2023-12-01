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
package org.adamalang.common.keys;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
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

  public static String generate(String masterKey) throws Exception {
    KeyPair pair = Jwts.SIG.ES256.keyPair().build();
    ObjectNode localKeyFile = Json.newJsonObject();
    localKeyFile.put("algo", "ES256");
    localKeyFile.put("private", new String(Base64.getEncoder().encode(pair.getPrivate().getEncoded())));
    localKeyFile.put("public", new String(Base64.getEncoder().encode(pair.getPublic().getEncoded())));
    return MasterKey.encrypt(masterKey, localKeyFile.toString());
  }

  public String signDocument(String space, String key, String agent) {
    return Jwts.builder().setSubject(agent).setIssuer("doc/" + space + "/" + key).signWith(privateKey).compact();
  }

  public void validateTokenThrows(String token) {
    Jwts.parser().verifyWith(publicKey).build().parse(token);
  }
}
