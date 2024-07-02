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
package org.adamalang.apikit.model;

import java.util.Set;

public enum Type {
  String, Boolean, Long, Integer, JsonObject, JsonObjectOrArray;

  public static Type of(String parameterType) throws Exception {
    switch (parameterType) {
      case "bool":
      case "boolean":
        return Boolean;
      case "str":
      case "string":
        return String;
      case "int":
      case "integer":
        return Integer;
      case "long":
        return Long;
      case "json-object":
        return JsonObject;
      case "json-object-or-array":
        return JsonObjectOrArray;
      default:
        throw new Exception("unknown parameter type:" + parameterType);
    }
  }

  public void dumpImports(Set<String> imports) {
    switch (this) {
      case JsonObject:
        imports.add("com.fasterxml.jackson.databind.node.ObjectNode");
        return;
      case JsonObjectOrArray:
        imports.add("com.fasterxml.jackson.databind.JsonNode");
        return;
    }
  }

  public String javaType() {
    switch (this) {
      case Boolean:
        return "Boolean";
      case String:
        return "String";
      case Long:
        return "Long";
      case Integer:
        return "Integer";
      case JsonObject:
        return "ObjectNode";
      case JsonObjectOrArray:
        return "JsonNode";
    }
    throw new RuntimeException("bug");
  }

  public String adamaType() {
    switch (this) {
      case Boolean:
        return "bool";
      case String:
        return "string";
      case Long:
        return "long";
      case Integer:
        return "int";
      case JsonObject:
        return "dynamic";
      case JsonObjectOrArray:
        return "dynamic";
    }
    throw new RuntimeException("bug");
  }

  public String readerMethod() {
    switch (this) {
      case Boolean:
        return "readBool";
      case String:
        return "readString";
      case Long:
        return "readLong";
      case Integer:
        return "readInteger";
      case JsonObject:
        return "readObject";
      case JsonObjectOrArray:
        return "readJsonNode";
    }
    throw new RuntimeException("bug");
  }

  public String writeMethod() {
    switch (this) {
      case Boolean:
      case String:
      case Long:
      case Integer:
        return "put";
      case JsonObject:
      case JsonObjectOrArray:
        return "set";
    }
    throw new RuntimeException("bug");
  }

  public String putMethod() {
    switch (this) {
      case JsonObject:
      case JsonObjectOrArray:
        return "set";
    }
    return "put";
  }

  public String typescriptType() {
    switch (this) {
      case Boolean:
        return "boolean";
      case String:
      case Long:
        return "string";
      case Integer:
        return "number";
      case JsonObject:
        return "any";
    }
    throw new RuntimeException("bug");
  }
}
