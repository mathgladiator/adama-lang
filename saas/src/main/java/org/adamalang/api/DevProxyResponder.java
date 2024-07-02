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
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.JsonLogger;
import org.adamalang.web.io.JsonResponder;

public class DevProxyResponder implements JsonResponder {
  private final JsonResponder responder;
  private final ObjectNode itemToLog;
  private final JsonLogger logger;

  public DevProxyResponder(JsonResponder responder, ObjectNode itemToLog, JsonLogger logger) {
    this.responder = responder;
    this.itemToLog = itemToLog;
    this.logger = logger;
  }

  @Override
  public void stream(String json) {
    responder.stream(json);
  }

  @Override
  public void finish(String json) {
    responder.finish(json);
    itemToLog.put("success", true);
    logger.log(itemToLog);
  }

  @Override
  public void error(ErrorCodeException ex) {
    responder.error(ex);
    itemToLog.put("success", false);
    itemToLog.put("failure-code", ex.code);
    logger.log(itemToLog);
  }
}
