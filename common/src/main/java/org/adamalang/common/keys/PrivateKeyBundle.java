/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
