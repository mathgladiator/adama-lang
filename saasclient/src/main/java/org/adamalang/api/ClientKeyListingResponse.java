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

/** generated class for the responder: key-listing */
public class ClientKeyListingResponse {
  public final ObjectNode _original;
  public final String key;
  public final String created;
  public final String updated;
  public final Integer seq;

  public ClientKeyListingResponse(ObjectNode response) {
    this._original = response;
    this.key = Json.readString(response, "key");
    this.created = Json.readString(response, "created");
    this.updated = Json.readString(response, "updated");
    this.seq = Json.readInteger(response, "seq");
  }
}
