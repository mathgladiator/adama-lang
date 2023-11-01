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
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

/** generated class for the responder: stats */
public class ClientStatsResponse {
  public final ObjectNode _original;
  public final String statKey;
  public final String statValue;
  public final String statType;

  public ClientStatsResponse(ObjectNode response) {
    this._original = response;
    this.statKey = Json.readString(response, "stat-key");
    this.statValue = Json.readString(response, "stat-value");
    this.statType = Json.readString(response, "stat-type");
  }
  public String toInternalJson() {
    ObjectNode _next = Json.newJsonObject();
    _next.put("statKey", statKey);
    _next.put("statValue", statValue);
    _next.put("statType", statType);
    return _next.toString();
  }
}
