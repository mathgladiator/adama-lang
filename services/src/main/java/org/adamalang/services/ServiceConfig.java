package org.adamalang.services;

import org.adamalang.common.ErrorCodeException;

import java.util.Map;

/** a service config for a space */
public class ServiceConfig {
  private final Map<String, Object> config;

  public ServiceConfig(Map<String, Object> config, String space) {
    this.config = config;
  }

  public String getDecryptedConfig(String key) throws ErrorCodeException {
    Object objVal = config.get(key);
    if (objVal == null || !(objVal instanceof String)) {
      throw new ErrorCodeException(-1);
    }
    String encryptedString = (String) objVal;
    // TODO: parse as id;public;cipher
    return null;
  }
}
