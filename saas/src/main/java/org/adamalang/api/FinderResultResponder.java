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

public class FinderResultResponder {
  public final JsonResponder responder;

  public FinderResultResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete(Long id, Integer locationType, String archive, String region, String machine, Boolean deleted) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("id", id);
    _obj.put("locationType", locationType);
    _obj.put("archive", archive);
    _obj.put("region", region);
    _obj.put("machine", machine);
    _obj.put("deleted", deleted);
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
