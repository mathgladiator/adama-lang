/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.keys;

import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Base64;

public class PublicPrivateKeyPartnershipTests {
  @Test
  public void flow() throws Exception {
    KeyPair a = PublicPrivateKeyPartnership.genKeyPair();
    KeyPair b = PublicPrivateKeyPartnership.genKeyPair();

    String x = Base64.getEncoder().encodeToString(PublicPrivateKeyPartnership.secretFrom(PublicPrivateKeyPartnership.keyPairFrom( //
        PublicPrivateKeyPartnership.publicKeyOf(a), //
        PublicPrivateKeyPartnership.privateKeyOf(b))));

    String y = Base64.getEncoder().encodeToString(PublicPrivateKeyPartnership.secretFrom(PublicPrivateKeyPartnership.keyPairFrom( //
        PublicPrivateKeyPartnership.publicKeyOf(b), //
        PublicPrivateKeyPartnership.privateKeyOf(a))));

    Assert.assertEquals(x, y);
  }

  @Test
  public void cipher() throws Exception {
    KeyPair x = PublicPrivateKeyPartnership.genKeyPair();
    byte[] secret = PublicPrivateKeyPartnership.secretFrom(x);
    String encrypted = PublicPrivateKeyPartnership.encrypt(secret, "This is a secret, yo");
    System.err.println(encrypted);
    System.err.println(PublicPrivateKeyPartnership.decrypt(secret, encrypted));
  }
}
