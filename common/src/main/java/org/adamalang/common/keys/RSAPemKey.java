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

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** simple rsa keys */
public class RSAPemKey {
  // ssh-keygen -t rsa -b 4096 -m PEM -f rsa.key.4096
  // openssl pkcs8 -topk8 -inform PEM -in rsa.key.4096 -out private_key.rsa.pem -nocrypt
  public static RSAPrivateKey privateFrom(String pem) throws Exception {
    String simple = pem
        .replaceAll("[\r\n]", "")
        .replaceAll("-----[BEGIND]* PRIVATE KEY-----", "");
    byte[] encoded = Base64.getDecoder().decode(simple.getBytes(StandardCharsets.UTF_8));
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
    return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
  }

  // openssl rsa -in rsa.key.4096 -pubout -outform PEM -out public_key.rsa.pem
  public static RSAPublicKey publicFrom(String pem) throws Exception {
    String simple = pem
        .replaceAll("[\r\n]", "")
        .replaceAll("-----[BEGIND]* PUBLIC KEY-----", "");
    byte[] encoded = Base64.getDecoder().decode(simple.getBytes(StandardCharsets.UTF_8));
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
    return (RSAPublicKey) keyFactory.generatePublic(keySpec);
  }
}
