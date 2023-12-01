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
package org.adamalang.runtime.security;

import io.jsonwebtoken.Jwts;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.security.Keystore;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;

public class KeystoreTests {
  @Test
  public void flow() throws Exception {
    Keystore ks = Keystore.parse("{}");
    Keystore.validate(Json.newJsonObject());
    String privateKey = ks.generate("a001");
    Keystore.parsePrivateKey(Json.parseJsonObject(privateKey));
    Keystore ks2 = Keystore.parse(ks.persist());
    PrivateKey signingKey = Keystore.parsePrivateKey(Json.parseJsonObject(privateKey));
    String token1 = Jwts.builder().subject("agent").issuer("a001").signWith(signingKey).compact();
    ks.validate("a001", token1);
    try {
      ks.validate("a002", token1);
      Assert.fail();
    } catch (ErrorCodeException ex) {
      Assert.assertEquals(916531, ex.code);
    }
    try {
      Keystore.parse("");
    } catch (ErrorCodeException ex) {
      Assert.assertEquals(998459, ex.code);
    }
    try {
      Keystore.parse("{\"x\":[]}");
    } catch (ErrorCodeException ex) {
      Assert.assertEquals(949307, ex.code);
    }
  }

  @Test
  public void bad_public_key() throws Exception {
    try {
      Keystore.parsePublicKey(Json.parseJsonObject("{}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(967735, ece.code);
    }
    try {
      Keystore.parsePublicKey(Json.parseJsonObject("{\"algo\":\"x\"}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(901179, ece.code);
    }
    try {
      Keystore.parsePublicKey(Json.parseJsonObject("{\"algo\":\"x\",\"bytes64\":\"123\"}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(907319, ece.code);
    }
    try {
      Keystore.parsePublicKey(Json.parseJsonObject("{\"algo\":\"x\",\"bytes64\":\""+ Base64.getEncoder().encode("XYZ".getBytes(StandardCharsets.UTF_8)) +"\"}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(987191, ece.code);
    }
    try {
      Keystore.parsePublicKey(Json.parseJsonObject("{\"algo\":\"ES256\",\"bytes64\":\""+ Base64.getEncoder().encode("XYZ".getBytes(StandardCharsets.UTF_8)) +"\"}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(987191, ece.code);
    }
  }

  @Test
  public void bad_private_key() throws Exception {
    try {
      Keystore.parsePrivateKey(Json.parseJsonObject("{}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(967735, ece.code);
    }
    try {
      Keystore.parsePrivateKey(Json.parseJsonObject("{\"algo\":\"x\"}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(901179, ece.code);
    }
    try {
      Keystore.parsePrivateKey(Json.parseJsonObject("{\"algo\":\"x\",\"bytes64\":\"123\"}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(907319, ece.code);
    }
    try {
      Keystore.parsePrivateKey(Json.parseJsonObject("{\"algo\":\"x\",\"bytes64\":\""+ Base64.getEncoder().encode("XYZ".getBytes(StandardCharsets.UTF_8)) +"\"}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(987191, ece.code);
    }
    try {
      Keystore.parsePrivateKey(Json.parseJsonObject("{\"algo\":\"ES256\",\"bytes64\":\""+ Base64.getEncoder().encode("XYZ".getBytes(StandardCharsets.UTF_8)) +"\"}"));
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(987191, ece.code);
    }
  }
}
