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
package org.adamalang.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/** convert a JSON object into a application/x-www-form-urlencoded string */
public class XWWWFormUrl {
  private static void pump(ObjectNode node, String prefix, ArrayList<String> output) {
    Iterator<Map.Entry<String, JsonNode>> it = node.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> field = it.next();
      if (field.getValue().isObject()) {
        pump((ObjectNode) field.getValue(), prefix + field.getKey() + ".", output);
      } else if (field.getValue().isArray()) {
        // TODO: figure this out
      } else {
        if (field.getValue().isTextual()) {
          output.add(prefix + field.getKey() + "=" + URL.encode(field.getValue().textValue(), false));
        } else {
          output.add(prefix + field.getKey() + "=" + URL.encode(field.getValue().toString(), false));
        }
      }
    }
  }

  public static String encode(ObjectNode node) {
    ArrayList<String> output = new ArrayList<>();
    pump(node, "", output);
    return String.join("&", output);
  }
}
