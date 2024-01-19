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
package org.adamalang.runtime.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

import java.util.Iterator;
import java.util.TreeSet;

/** sum a bunch of Objects that have objects/ints within */
public class JsonSum {

  private static void mergeSum(ObjectNode basis, ObjectNode arg) {
    TreeSet<String> fields = new TreeSet<>();
    Iterator<String> it = arg.fieldNames();
    while (it.hasNext()) {
      fields.add(it.next());
    }
    for (String field: fields) {
      JsonNode left = basis.get(field);
      JsonNode right = arg.get(field);
      if (left == null || left.isNull()) {
        basis.set(field, right);
      } else if (left.isObject() && right.isObject()) {
        mergeSum((ObjectNode) left, (ObjectNode) right);
      } else if (left.isIntegralNumber() && right.isIntegralNumber()) {
        basis.put(field, left.intValue() + right.intValue());
      }
    }
  }

  public static ObjectNode sum(ObjectNode... args) {
    ObjectNode basis = Json.newJsonObject();
    for (ObjectNode arg : args) {
      mergeSum(basis, arg);
    }
    return basis;
  }

  public static ObjectNode sum(Iterable<ObjectNode> args) {
    ObjectNode basis = Json.newJsonObject();
    for (ObjectNode arg : args) {
      mergeSum(basis, arg);
    }
    return basis;
  }
}
