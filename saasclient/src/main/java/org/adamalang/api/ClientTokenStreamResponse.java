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

/** generated class for the responder: token-stream */
public class ClientTokenStreamResponse {
  public final ObjectNode _original;
  public final Long id;
  public final ObjectNode subscriptionInfo;
  public final ObjectNode deviceInfo;

  public ClientTokenStreamResponse(ObjectNode response) {
    this._original = response;
    this.id = Json.readLong(response, "id");
    this.subscriptionInfo = Json.readObject(response, "subscription-info");
    this.deviceInfo = Json.readObject(response, "device-info");
  }
  public String toInternalJson() {
    ObjectNode _next = Json.newJsonObject();
    _next.put("id", id);
    _next.set("subscriptionInfo", subscriptionInfo);
    _next.set("deviceInfo", deviceInfo);
    return _next.toString();
  }
}
