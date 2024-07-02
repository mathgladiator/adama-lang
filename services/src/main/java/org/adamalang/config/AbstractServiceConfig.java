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
import org.adamalang.runtime.remote.ServiceConfig;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** implement ServiceConfig for everything except secrets */
public abstract class AbstractServiceConfig implements ServiceConfig  {
  protected final String space;
  protected final Map<String, Object> config;

  public AbstractServiceConfig(String space, Map<String, Object> config) {
    this.space = space;
    this.config = config;
  }

  @Override
  public String getSpace() {
    return space;
  }

  @Override
  public Set<String> getKeys() {
    return new TreeSet<>(config.keySet());
  }

  @Override
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

  @Override
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
}
