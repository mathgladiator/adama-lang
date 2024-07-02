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
package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

/** simple tooling to walk the fields and find unused fields by the lacking of the used marking */
public interface UnusedReport {
  public void reportUnused(String type, String field);

  /** drive the reporting, this forest MUST come from post-typing since there is a side effect in the ObjectNode tree */
  public static void drive(ObjectNode forest, UnusedReport report) {
    Iterator<Map.Entry<String, JsonNode>> itTypes = forest.get("types").fields();
    while (itTypes.hasNext()) {
      Map.Entry<String, JsonNode> struct = itTypes.next();
      Iterator<Map.Entry<String, JsonNode>> fieldTypes = struct.getValue().get("fields").fields();
      while (fieldTypes.hasNext()) {
        Map.Entry<String, JsonNode> field = fieldTypes.next();
        if (!field.getValue().has("used")) {
          report.reportUnused(struct.getKey(), field.getKey());
        }
      }
    }
  }
}
