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
package org.adamalang.cli.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.LogTimestamp;

/** experimental logger layout for sending to another service */
public class JsonLayout extends LayoutBase<ILoggingEvent> {
  @Override
  public String doLayout(ILoggingEvent e) {
    ObjectNode obj = Json.newJsonObject();
    obj.put("@timestamp", LogTimestamp.now());
    obj.put("thread", e.getThreadName());
    obj.put("level", e.getLevel().toString());
    obj.put("message", e.getMessage());
    return obj.toString();
  }
}
