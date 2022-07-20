/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common.keys;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.XECPrivateKey;
import java.security.interfaces.XECPublicKey;
import java.security.spec.NamedParameterSpec;
import java.security.spec.XECPrivateKeySpec;
import java.security.spec.XECPublicKeySpec;
import java.util.Base64;

/** Simplifies working with X25519 where keys are stored serialized form */
public class PublicPrivateKeyPartnership {
  /** generate a new key-pair */
  public static KeyPair genKeyPair() throws Exception {
    return KeyPairGenerator.getInstance("X25519").generateKeyPair();
  }

  /** serialize the public key of the given key pair */
  public static String publicKeyOf(KeyPair keyPair) {
    return Base64.getEncoder().encodeToString(((XECPublicKey) keyPair.getPublic()).getU().toByteArray());
  }

  /** serialize the private key of the given key pair */
  public static String privateKeyOf(KeyPair keyPair) {
    return Base64.getEncoder().encodeToString(((XECPrivateKey) keyPair.getPrivate()).getScalar().orElseThrow());
  }

  /** build a key pair from the given serialized public key and private key */
  public static KeyPair keyPairFrom(String publicKey, String privateKey) throws Exception {
    byte[] rawPublicKey = Base64.getDecoder().decode(publicKey);
    byte[] rawPrivateKey = Base64.getDecoder().decode(privateKey);
    KeyFactory kf = KeyFactory.getInstance("XDH");
    NamedParameterSpec spec = new NamedParameterSpec("X25519");
    PublicKey pubKey = kf.generatePublic(new XECPublicKeySpec(spec, new BigInteger(rawPublicKey)));
    PrivateKey prvKey = kf.generatePrivate(new XECPrivateKeySpec(spec, rawPrivateKey));
    return new KeyPair(pubKey, prvKey);
  }

  /** derive a secret from the given key pair */
  public static byte[] secretFrom(KeyPair pair) throws Exception {
    KeyAgreement ka = KeyAgreement.getInstance("XDH");
    ka.init(pair.getPrivate());
    ka.doPhase(pair.getPublic(), true);
    return ka.generateSecret();
  }

  /** basic AES encryption */
  public static String encrypt(byte[] secret, String plaintext) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
    SecretKeySpec key = new SecretKeySpec(secret, "AES");
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    byte[] iv = new byte[16];
    new SecureRandom().nextBytes(iv);
    cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
    memory.write(iv);
    memory.write(cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8)));
    memory.flush();
    memory.close();
    return Base64.getEncoder().encodeToString(memory.toByteArray());
  }

  /** basic AES decryption */
  public static String decrypt(byte[] secret, String encrypted) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
    SecretKeySpec key = new SecretKeySpec(secret, "AES");
    byte[] memory = Base64.getDecoder().decode(encrypted.getBytes(StandardCharsets.UTF_8));
    byte[] iv = new byte[16];
    System.arraycopy(memory, 0, iv, 0, 16);
    cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
    return new String(cipher.doFinal(memory, 16, memory.length - 16), StandardCharsets.UTF_8);
  }
}
