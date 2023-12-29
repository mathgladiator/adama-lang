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
package org.adamalang.devbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.config.AbstractServiceConfig;

import java.util.Map;
import java.util.function.Consumer;

public class DevBoxServiceConfig extends AbstractServiceConfig {
  private final ObjectNode secrets;
  private final String service;
  private final Consumer<String> logger;

  public DevBoxServiceConfig(String space, Map<String, Object> config, ObjectNode secrets, String service, Consumer<String> logger) {
    super(space, config);
    this.secrets = secrets;
    this.service = service;
    this.logger = logger;
  }

  @Override
  public String getDecryptedSecret(String key) throws ErrorCodeException {
    String secret = Json.readString(secrets, key);
    if (secret == null) {
      logger.accept("devservices|failed find-secret for '" + key + "' in '" + service + "'");
      throw new ErrorCodeException(ErrorCodes.DEVBOX_NO_SECRET_FOR_KEY);
    }
    return secret;
  }
}
