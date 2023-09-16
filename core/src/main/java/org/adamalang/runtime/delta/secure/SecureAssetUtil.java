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
package org.adamalang.runtime.delta.secure;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecureAssetUtil {
  public static String encryptToBase64(SecretKey key, String id) {
    try {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] bytes = cipher.doFinal(id.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(bytes);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String decryptFromBase64(SecretKey key, String base64) {
    try {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(base64));
      return new String(plainText, StandardCharsets.UTF_8);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static SecretKey secretKeyOf(String base64) throws ErrorCodeException {
    byte[] raw = Base64.getDecoder().decode(base64);
    if (raw.length != 24) {
      throw new ErrorCodeException(ErrorCodes.ASSET_KEY_WRONG_LENGTH);
    }
    return new SecretKey() {
      @Override
      public String getAlgorithm() {
        return "AES";
      }

      @Override
      public String getFormat() {
        return "RAW";
      }

      @Override
      public byte[] getEncoded() {
        return raw;
      }
    };
  }

  public static String makeAssetKeyHeader() {
    KeyGenerator keygen = getKeyGenerator("AES");
    keygen.init(192);
    SecretKey key = keygen.generateKey();
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  public static KeyGenerator getKeyGenerator(String algorithm) {
    try {
      return KeyGenerator.getInstance(algorithm);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
