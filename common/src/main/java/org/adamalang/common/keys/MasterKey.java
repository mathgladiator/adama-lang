/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.keys;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/** Wrapper for the master key used to protect the database with the host key; this is an alternative to using key-management */
public class MasterKey {
  public static String generateMasterKey() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] key = new byte[256 / 8];
    secureRandom.nextBytes(key);
    return Base64.getEncoder().encodeToString(key);
  }

  public static String encrypt(String masterKey, String value) throws Exception {
    byte[] key = Base64.getDecoder().decode(masterKey);
    Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
    SecureRandom secureRandom = new SecureRandom();
    byte[] nonce = new byte[96 / 8];
    secureRandom.nextBytes(nonce);
    byte[] iv = new byte[128 / 8];
    System.arraycopy(nonce, 0, iv, 0, nonce.length);
    Key keySpec = new SecretKeySpec(key, "AES");
    IvParameterSpec ivSpec = new IvParameterSpec(iv);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
    byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(nonce) + ";" + Base64.getEncoder().encodeToString(encrypted);
  }

  public static String decrypt(String masterKey, String value) throws Exception {
    byte[] key = Base64.getDecoder().decode(masterKey);
    Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
    String[] parts = value.split(";");
    byte[] nonce = Base64.getDecoder().decode(parts[0]);
    byte[] encrypted = Base64.getDecoder().decode(parts[1]);
    byte[] iv = new byte[128 / 8];
    System.arraycopy(nonce, 0, iv, 0, nonce.length);
    Key keySpec = new SecretKeySpec(key, "AES");
    IvParameterSpec ivSpec = new IvParameterSpec(iv);
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
    return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
  }
}
