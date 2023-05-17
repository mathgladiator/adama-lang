/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.services;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Secrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Pattern;

/** a service config for a space */
public class ServiceConfig {
  private final Logger LOGGER = LoggerFactory.getLogger(ServiceConfig.class);
  private final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(ServiceConfig.class);
  private final DataBase dataBase;
  private final String space;
  private final Map<String, Object> config;
  private final String masterKey;

  public ServiceConfig(DataBase dataBase, String space, Map<String, Object> config, String masterKey) {
    this.dataBase = dataBase;
    this.config = config;
    this.space = space;
    this.masterKey = masterKey;
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
      privateKey = MasterKey.decrypt(masterKey, Secrets.getPrivateKey(dataBase, space, keyId));
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
