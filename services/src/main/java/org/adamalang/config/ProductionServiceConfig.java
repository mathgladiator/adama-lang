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
package org.adamalang.config;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.PrivateKeyBundle;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** a service config for a space */
public class ProductionServiceConfig extends AbstractServiceConfig {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(ProductionServiceConfig.class);
  private final TreeMap<Integer, PrivateKeyBundle> keys;

  public ProductionServiceConfig(String space, Map<String, Object> config, TreeMap<Integer, PrivateKeyBundle> keys) {
    super(space, config);
    this.keys = keys;
  }

  @Override
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
