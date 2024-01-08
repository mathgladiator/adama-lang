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

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class DomainRawResponder {
  public final JsonResponder responder;

  public DomainRawResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete(String domain, Integer owner, String space, String key, String forward, Boolean route, String certificate, Long timestamp) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("domain", domain);
    _obj.put("owner", owner);
    _obj.put("space", space);
    _obj.put("key", key);
    _obj.put("forward", forward);
    _obj.put("route", route);
    _obj.put("certificate", certificate);
    _obj.put("timestamp", timestamp);
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
