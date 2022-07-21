/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.services;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Secrets;

import java.util.Map;
import java.util.regex.Pattern;

/** a service config for a space */
public class ServiceConfig {
  private final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(ServiceConfig.class);
  private final DataBase dataBase;
  private final String space;
  private final Map<String, Object> config;

  public ServiceConfig(DataBase dataBase, String space, Map<String, Object> config) {
    this.dataBase = dataBase;
    this.config = config;
    this.space = space;
  }

  public int getInteger(String key, int defaultValue) throws ErrorCodeException {
    Object objVal = config.get(key);
    if (objVal instanceof String) {
      try {
        return Integer.parseInt((String) objVal);
      } catch (NumberFormatException nfe) {
        throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_INTEGER_FROM_STRING);
      }
    }
    if (objVal instanceof Integer) {
      return (Integer) objVal;
    }
    if (objVal == null) {
      return defaultValue;
    }
    throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_INTEGER);
  }

  public String getString(String key, String defaultValue) throws ErrorCodeException {
    Object objVal = config.get(key);
    if (objVal instanceof String) {
      return (String) objVal;
    }
    if (objVal == null && defaultValue != null) {
      return defaultValue;
    }
    if (objVal != null) {
      throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_STRING_TYPE);
    } else {
      throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_STRING_NOT_PRESENT);
    }
  }

  public String getDecryptedSecret(String key) throws ErrorCodeException {
    Object objVal = config.get(key);
    if (objVal == null || !(objVal instanceof String)) {
      throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_ENCRYPT_STRING_NOT_PRESENT_OR_WRONG_TYPE);
    }
    String encryptedString = (String) objVal;
    String[] parts = encryptedString.split(Pattern.quote(";"));
    if (parts.length != 3) {
      throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_ENCRYPT_STRING_NO_KEYID_ETC);
    }
    int keyId;
    try {
      keyId = Integer.parseInt(parts[0]);
    } catch (NumberFormatException nfe) {
      throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_ENCRYPT_STRING_KEYID_INVALID, nfe);
    }
    String publicKey = parts[1];
    String privateKey;
    try {
      privateKey = Secrets.getPrivateKey(dataBase, space, keyId);
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.SERVICE_CONFIG_BAD_ENCRYPT_STRING_FAILED_SECRET_KEY_LOOKUP, ex, EXLOGGER);
    }
    byte[] secret;
    try {
      secret = PublicPrivateKeyPartnership.secretFrom(PublicPrivateKeyPartnership.keyPairFrom(publicKey, privateKey));
      return PublicPrivateKeyPartnership.decrypt(secret, parts[2]);
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.SERVICE_CONFIG_BAD_ENCRYPT_STRING_FAILED_DECRYPTION, ex, EXLOGGER);
    }
  }
}
