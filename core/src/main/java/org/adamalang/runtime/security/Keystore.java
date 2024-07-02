/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

/** a collection of keys used under an authority to validate a signed key */
public class Keystore {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(Keystore.class);
  private final ArrayList<PublicKey> keys;
  private PublicKey mostRecentKey;

  private Keystore(ObjectNode node) throws ErrorCodeException {
    this.keys = new ArrayList<>();
    Iterator<Map.Entry<String, JsonNode>> it = node.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> entry = it.next();
      if (entry.getValue() instanceof ObjectNode) {
        mostRecentKey = parsePublicKey((ObjectNode) entry.getValue());
        keys.add(mostRecentKey);
      } else {
        throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_ROOT_ITEM_NOT_OBJECT);
      }
    }
  }

  public String persist() {
    ObjectNode bag = Json.newJsonObject();
    int index = 0;
    for (PublicKey key : keys) {
      String bytes64 = new String(Base64.getEncoder().encode(key.getEncoded()));
      ObjectNode keyNode = bag.putObject("" + index);
      keyNode.put("algo", key.getAlgorithm());
      keyNode.put("bytes64", bytes64);
      index++;
    }
    return bag.toString();
  }

  public String generate(String authority) {
    KeyPair pair = Jwts.SIG.ES256.keyPair().build();
    ObjectNode localKeyFile = Json.newJsonObject();
    localKeyFile.put("authority", authority);
    localKeyFile.put("algo", "ES256");
    localKeyFile.put("bytes64", new String(Base64.getEncoder().encode(pair.getPrivate().getEncoded())));
    keys.add(pair.getPublic());
    return localKeyFile.toString();
  }

  public static PrivateKey parsePrivateKey(ObjectNode node) throws ErrorCodeException {
    JsonNode alogNode = node.get("algo");
    JsonNode bytes64node = node.get("bytes64");
    if (alogNode == null || alogNode.isNull() || !alogNode.isTextual()) {
      throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_ALGO);
    }
    String algo = alogNode.textValue();
    if (bytes64node == null || bytes64node.isNull() || !bytes64node.isTextual()) {
      throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_BYTES64);
    }
    byte[] bytes;
    try {
      bytes = Base64.getDecoder().decode(bytes64node.textValue());
    } catch (IllegalArgumentException iae) {
      throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_VALID_BYTES64);
    }
    try {
      switch (algo) {
        case "ES256":
          return KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(bytes));
        default:
          throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_VALID_ALGO);
      }
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.API_KEYSTORE_KEY_INTERNAL_ERROR, ex, LOGGER);
    }
  }

  public static PublicKey parsePublicKey(ObjectNode node) throws ErrorCodeException {
    JsonNode alogNode = node.get("algo");
    JsonNode bytes64node = node.get("bytes64");
    if (alogNode == null || alogNode.isNull() || !alogNode.isTextual()) {
      throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_ALGO);
    }
    String algo = alogNode.textValue();
    if (bytes64node == null || bytes64node.isNull() || !bytes64node.isTextual()) {
      throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_BYTES64);
    }
    byte[] bytes;
    try {
      bytes = Base64.getDecoder().decode(bytes64node.textValue());
    } catch (IllegalArgumentException iae) {
      throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_VALID_BYTES64);
    }
    try {
      switch (algo) {
        case "EC":
        case "ES256":
          return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(bytes));
        default:
          throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_VALID_ALGO);
      }
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.API_KEYSTORE_KEY_INTERNAL_ERROR, ex, LOGGER);
    }
  }

  public static void validate(ObjectNode node) throws ErrorCodeException {
    new Keystore(node);
  }

  public static Keystore parse(String json) throws ErrorCodeException {
    try {
      return new Keystore(Json.parseJsonObject(json));
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.API_KEYSTORE_NOT_JSON, ex, LOGGER);
    }
  }

  public NtPrincipal validate(String authority, String identity) throws ErrorCodeException {
    for (PublicKey publicKey : keys) {
      try {
        Jws<Claims> claims = Jwts.parser()
            .verifyWith(publicKey)
            .requireIssuer(authority)
            .build()
            .parseSignedClaims(identity);
        return new NtPrincipal(claims.getPayload().getSubject(), authority);
      } catch (Exception ex) {
        // move on
      }
    }
    throw new ErrorCodeException(ErrorCodes.AUTH_FAILED_VALIDATING_AGAINST_KEYSTORE);
  }
}
