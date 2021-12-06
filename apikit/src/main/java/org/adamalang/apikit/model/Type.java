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
