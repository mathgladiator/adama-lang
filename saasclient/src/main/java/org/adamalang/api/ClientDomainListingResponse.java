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
import org.adamalang.common.Json;

/** generated class for the responder: domain-listing */
public class ClientDomainListingResponse {
  public final ObjectNode _original;
  public final String domain;
  public final String space;
  public final String key;
  public final Boolean route;
  public final String forward;
  public final Boolean configured;
  public final Boolean apex_managed;

  public ClientDomainListingResponse(ObjectNode response) {
    this._original = response;
    this.domain = Json.readString(response, "domain");
    this.space = Json.readString(response, "space");
    this.key = Json.readString(response, "key");
    this.route = Json.readBool(response, "route");
    this.forward = Json.readString(response, "forward");
    this.configured = Json.readBool(response, "configured");
    this.apex_managed = Json.readBool(response, "apex_managed");
  }
  public String toInternalJson() {
    ObjectNode _next = Json.newJsonObject();
    _next.put("domain", domain);
    _next.put("space", space);
    _next.put("key", key);
    _next.put("route", route);
    _next.put("forward", forward);
    _next.put("configured", configured);
    _next.put("apex_managed", apex_managed);
    return _next.toString();
  }
}
