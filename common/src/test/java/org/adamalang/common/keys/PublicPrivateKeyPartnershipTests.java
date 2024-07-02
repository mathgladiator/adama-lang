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
