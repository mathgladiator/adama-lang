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

/** generated class for the responder: billing-usage */
public class ClientBillingUsageResponse {
  public final ObjectNode _original;
  public final Integer hour;
  public final Long cpu;
  public final Long memory;
  public final Integer connections;
  public final Integer documents;
  public final Integer messages;
  public final Long storageBytes;
  public final Long bandwidth;
  public final Long firstPartyServiceCalls;
  public final Long thirdPartyServiceCalls;

  public ClientBillingUsageResponse(ObjectNode response) {
    this._original = response;
    this.hour = Json.readInteger(response, "hour");
    this.cpu = Json.readLong(response, "cpu");
    this.memory = Json.readLong(response, "memory");
    this.connections = Json.readInteger(response, "connections");
    this.documents = Json.readInteger(response, "documents");
    this.messages = Json.readInteger(response, "messages");
    this.storageBytes = Json.readLong(response, "storage-bytes");
    this.bandwidth = Json.readLong(response, "bandwidth");
    this.firstPartyServiceCalls = Json.readLong(response, "first-party-service-calls");
    this.thirdPartyServiceCalls = Json.readLong(response, "third-party-service-calls");
  }
}
