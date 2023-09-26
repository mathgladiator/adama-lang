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

/** generated class for the responder: space-listing */
public class ClientSpaceListingResponse {
  public final ObjectNode _original;
  public final String space;
  public final String role;
  public final String created;
  public final Boolean enabled;
  public final Long storageBytes;

  public ClientSpaceListingResponse(ObjectNode response) {
    this._original = response;
    this.space = Json.readString(response, "space");
    this.role = Json.readString(response, "role");
    this.created = Json.readString(response, "created");
    this.enabled = Json.readBool(response, "enabled");
    this.storageBytes = Json.readLong(response, "storage-bytes");
  }
  public String toInternalJson() {
    ObjectNode _next = Json.newJsonObject();
    _next.put("space", space);
    _next.put("role", role);
    _next.put("created", created);
    _next.put("enabled", enabled);
    _next.put("storageBytes", storageBytes);
    return _next.toString();
  }
}
