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
package org.adamalang.services;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Secrets;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** a service config for a space */
public class ServiceConfig {
  private final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(ServiceConfig.class);
  private final TreeMap<Integer, PrivateKeyBundle> keys;
  private final String space;
  private final Map<String, Object> config;

  public ServiceConfig(String space, Map<String, Object> config, TreeMap<Integer, PrivateKeyBundle> keys) {
    this.space = space;
    this.config = config;
    this.keys = keys;
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
    PrivateKeyBundle bundle;
    try {
      bundle = keys.get(Integer.parseInt(parts[0]));
      if (bundle == null) {
        throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_PRIVATE_KEY_BUNDLE);
      }
    } catch (NumberFormatException nfe) {
      throw new ErrorCodeException(ErrorCodes.SERVICE_CONFIG_BAD_ENCRYPT_STRING_KEYID_INVALID, nfe);
    }
    try {
      return bundle.decrypt(parts[1], parts[2]);
    } catch (Exception ex) {
      throw ErrorCodeException.detectOrWrap(ErrorCodes.SERVICE_CONFIG_BAD_ENCRYPT_STRING_FAILED_DECRYPTION, ex, EXLOGGER);
    }
  }
}
