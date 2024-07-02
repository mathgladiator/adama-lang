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
