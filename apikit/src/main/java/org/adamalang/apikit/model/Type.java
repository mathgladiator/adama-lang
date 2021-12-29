/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.apikit.model;

public enum Type {
    String,
    Boolean,
    Long,
    Integer,
    JsonObject;

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
        }
        throw new RuntimeException();
    }

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
            default:
                throw new Exception("unknown parameter type:" + parameterType);
        }
    }
}
