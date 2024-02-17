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
package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.function.Consumer;

/** the tuple of a scope and a type */
public class DataSelector {
  public final DataScope scope;
  public final ObjectNode type;

  public DataSelector(DataScope scope, ObjectNode type) {
    this.scope = scope;
    this.type = type;
  }

  public DataScope scopeInto(Consumer<String> errors) {
    try {
      String nature = type.get("nature").textValue();
      if (nature.equals("reactive_ref") || nature.equals("native_ref")) {
        String ref = type.get("ref").textValue();
        return scope.push(ref);
      }
      errors.accept("failed to scope into: " + type);
      return null;
    } catch (Exception reasonsToHateJSON) {
      errors.accept("error parsing reflection tree:" + reasonsToHateJSON.getMessage() + " JSON=" + type.toString());
      return null;
    }
  }

  public DataScope iterateInto(Consumer<String> errors) {
    try {
      String nature = type.get("nature").textValue();
      if (nature.equals("native_list")) {
        ObjectNode subType = (ObjectNode) type.get("type");
        String subNature = subType.get("nature").textValue();
        if (subNature.equals("reactive_ref") || subNature.equals("native_ref")) {
          String ref = subType.get("ref").textValue();
          return scope.push("list:" + ref, ref);
        }
      }
      errors.accept("failed to iterate into: " + type);
      return null;
    } catch (Exception reasonsToHateJSON) {
      errors.accept("error parsing reflection tree:" + reasonsToHateJSON.getMessage() + " JSON=" + type.toString());
      return null;
    }
  }
}
