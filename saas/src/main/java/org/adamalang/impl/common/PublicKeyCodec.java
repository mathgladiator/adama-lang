/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.impl.common;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** helper for auth */
public class PublicKeyCodec {

  /** Decode a public key stored from within a database */
  public static PublicKey decode(String publicKey64) throws Exception {
    byte[] publicKey = Base64.getDecoder().decode(publicKey64);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
    KeyFactory kf = KeyFactory.getInstance("EC");
    return kf.generatePublic(spec);
  }
  /** Encode a public key to store within a database */
  public static String encodePublicKey(KeyPair pair) {
    return new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
  }

  /** Invent a key pair for the host to bind to */
  public static KeyPair inventHostKey() {
    return Keys.keyPairFor(SignatureAlgorithm.ES256);
  }

}
