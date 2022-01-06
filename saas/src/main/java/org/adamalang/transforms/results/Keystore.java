/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.transforms.results;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

public class Keystore {
  private final ArrayList<PublicKey> keys;

  private Keystore(ObjectNode node) throws ErrorCodeException {
    this.keys = new ArrayList<>();
    Iterator<Map.Entry<String, JsonNode>> it = node.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> entry = it.next();
      if (entry.getValue() instanceof ObjectNode) {
        keys.add(parsePublicKey((ObjectNode) entry.getValue()));
      } else {
        throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_ROOT_ITEM_NOT_OBJECT);
      }
    }
  }

  private static PublicKey parsePublicKey(ObjectNode node) throws ErrorCodeException {
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
    X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
    try {
      KeyFactory kf = null;
      switch (algo) {
        case "EC":
          KeyFactory.getInstance("EC");
          break;
        case "RSA":
          KeyFactory.getInstance("RSA");
          break;
        default:
          throw new ErrorCodeException(ErrorCodes.API_KEYSTORE_KEY_LACKS_VALID_ALGO);
      }
      return kf.generatePublic(spec);
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.API_KEYSTORE_KEY_INTERNAL_ERROR, ex);
    }
  }

  public static Keystore parse(String json) throws ErrorCodeException {
    try {
      return new Keystore(Json.parseJsonObject(json));
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.API_KEYSTORE_NOT_JSON, ex);
    }
  }

  public boolean validate(String authority, String identity) {
    for (PublicKey publicKey : keys) {
      try {
        Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer(authority)
            .build()
            .parseClaimsJws(identity);
        return true;
      } catch (Exception ex) {
        // move on
      }
    }
    return false;
  }
}
