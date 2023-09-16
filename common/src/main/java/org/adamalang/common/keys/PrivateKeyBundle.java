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

/** a private key that has been bundled */
public class PrivateKeyBundle {
  private final String privateKey;

  private PrivateKeyBundle(String privateKey) {
    this.privateKey = privateKey;
  }

  // TODO: I'd like to consider optimizing the decrypt process OR locking it down more
  /*
byte[] rawPublicKey = Base64.getDecoder().decode(publicKey);
byte[] rawPrivateKey = Base64.getDecoder().decode(privateKey);
KeyFactory kf = KeyFactory.getInstance("XDH");
NamedParameterSpec spec = new NamedParameterSpec("X25519");
PublicKey pubKey = kf.generatePublic(new XECPublicKeySpec(spec, new BigInteger(rawPublicKey)));
PrivateKey prvKey = kf.generatePrivate(new XECPrivateKeySpec(spec, rawPrivateKey));
return new KeyPair(pubKey, prvKey);
*/

  public static PrivateKeyBundle fromDisk(String encryptedPrivateKey, String masterKey) throws Exception {
    return new PrivateKeyBundle(MasterKey.decrypt(masterKey, encryptedPrivateKey));
  }

  public static PrivateKeyBundle fromNetwork(String privateKey) {
    return new PrivateKeyBundle(privateKey);
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public String decrypt(String publicKey, String cipher) throws Exception {
    byte[] secret = PublicPrivateKeyPartnership.secretFrom(PublicPrivateKeyPartnership.keyPairFrom(publicKey, privateKey));
    return PublicPrivateKeyPartnership.decrypt(secret, cipher);
  }
}
