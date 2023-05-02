/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.model;

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
