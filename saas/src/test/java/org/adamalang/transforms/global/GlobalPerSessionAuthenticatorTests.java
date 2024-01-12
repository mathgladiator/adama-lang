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
package org.adamalang.transforms.global;

import io.jsonwebtoken.Jwts;
import org.adamalang.auth.GlobalAuthenticator;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.frontend.Session;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.impl.global.GlobalPerSessionAuthenticator;
import org.adamalang.contracts.data.ParsedToken;
import org.adamalang.auth.AuthenticatedUser;
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
    GlobalAuthenticator gAuth = new GlobalAuthenticator(null, SimpleExecutor.NOW);
    GlobalPerSessionAuthenticator authenticator = new GlobalPerSessionAuthenticator(null, gAuth, new ConnectionContext("a", "b", "c", null), new String[] {}, new String[] {});
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
    GlobalAuthenticator gAuth = new GlobalAuthenticator(null, SimpleExecutor.NOW);
    KeyPair pair = Jwts.SIG.ES256.keyPair().build();
    String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
    String token = Jwts.builder().subject("super").issuer("super").signWith(pair.getPrivate()).compact();
    GlobalPerSessionAuthenticator authenticator = new GlobalPerSessionAuthenticator(null, gAuth, new ConnectionContext("a", "b", "c", null), new String[] { publicKey }, new String[] {});
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
    GlobalAuthenticator gAuth = new GlobalAuthenticator(null, SimpleExecutor.NOW);
    KeyPair pair = Jwts.SIG.ES256.keyPair().build();
    String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
    String token = Jwts.builder().setSubject("xyz").setIssuer("region").signWith(pair.getPrivate()).compact();
    GlobalPerSessionAuthenticator authenticator = new GlobalPerSessionAuthenticator(null, gAuth, new ConnectionContext("a", "b", "c", null), new String[] {  }, new String[] {publicKey});
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
