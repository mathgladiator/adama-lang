/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.transforms.global;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.frontend.Session;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.impl.global.GlobalPerSessionAuthenticator;
import org.adamalang.contracts.data.ParsedToken;
import org.adamalang.contracts.data.AuthenticatedUser;
import org.adamalang.web.io.ConnectionContext;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GlobalPerSessionAuthenticatorTests {
  @Test
  public void tokenParsing() {
    try {
      new ParsedToken("{}");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(995342, ece.code);
    }
    try {
      new ParsedToken("x.x.x");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(908303, ece.code);
    }
  }

  @Test
  public void keys() throws Exception {
    KeyPair hostKeyPair = PublicKeyCodec.inventHostKey();
    PublicKeyCodec.decode(PublicKeyCodec.encodePublicKey(hostKeyPair));
  }

  @Test
  public void anonymous() throws Exception {
    GlobalPerSessionAuthenticator authenticator = new GlobalPerSessionAuthenticator(null, "masterkey", new ConnectionContext("a", "b", "c", "D"), new String[] {}, new String[] {});
    Assert.assertEquals("D", authenticator.assetKey());
    authenticator.updateAssetKey("E");
    Assert.assertEquals("E", authenticator.assetKey());
    Session session = new Session(authenticator);
    CountDownLatch success = new CountDownLatch(1);
    authenticator.execute(session, "anonymous:jeffrey", new Callback<AuthenticatedUser>() {
      @Override
      public void success(AuthenticatedUser who) {
        Assert.assertEquals("jeffrey", who.who.agent);
        Assert.assertEquals("anonymous", who.who.authority);
        success.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(success.await(1000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void superUser() throws Exception {
    KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
    String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
    String token = Jwts.builder().setSubject("super").setIssuer("super").signWith(pair.getPrivate()).compact();
    GlobalPerSessionAuthenticator authenticator = new GlobalPerSessionAuthenticator(null, "masterkey", new ConnectionContext("a", "b", "c", "D"), new String[] { publicKey }, new String[] {});
    CountDownLatch latch = new CountDownLatch(1);
    authenticator.execute(new Session(authenticator), token, new Callback<AuthenticatedUser>() {
      @Override
      public void success(AuthenticatedUser value) {
        Assert.assertEquals("super", value.who.authority);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
        latch.countDown();
        Assert.fail();
      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void regionalKey() throws Exception {
    KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
    String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
    String token = Jwts.builder().setSubject("xyz").setIssuer("region").signWith(pair.getPrivate()).compact();
    GlobalPerSessionAuthenticator authenticator = new GlobalPerSessionAuthenticator(null, "masterkey", new ConnectionContext("a", "b", "c", "D"), new String[] {  }, new String[] {publicKey});
    CountDownLatch latch = new CountDownLatch(1);
    authenticator.execute(new Session(authenticator), token, new Callback<AuthenticatedUser>() {
      @Override
      public void success(AuthenticatedUser value) {
        Assert.assertEquals("region", value.who.authority);
        Assert.assertEquals("xyz", value.who.agent);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
        latch.countDown();
        Assert.fail();
      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
  }
}
