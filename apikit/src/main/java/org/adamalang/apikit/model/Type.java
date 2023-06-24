/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
