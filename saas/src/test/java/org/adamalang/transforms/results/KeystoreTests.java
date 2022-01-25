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

import io.jsonwebtoken.Jwts;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
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
    String token1 = Jwts.builder().setSubject("agent").setIssuer("a001").signWith(signingKey).compact();
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
