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
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AssembleDevBox {
  public static String make(String packageName, Method[] unfilteredMethods) {
    final Method[] methods;
    {
      ArrayList<Method> filtered = new ArrayList<>();
      for (Method method : unfilteredMethods) {
        if (method.devbox) {
          filtered.add(method);
        }
      }
      methods = filtered.toArray(new Method[filtered.size()]);
    }

    HashMap<String, ArrayList<Method>> methodsBySubHandler = AssembleHandlers.shred(methods);
    StringBuilder router = new StringBuilder();
    router.append("package ").append(packageName).append(";\n\n");
    router.append("import com.fasterxml.jackson.databind.JsonNode;\n");
    router.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
    router.append("import org.adamalang.common.*;\n");
    router.append("import org.adamalang.web.io.*;\n");
    router.append("import org.adamalang.ErrorCodes;\n");
    router.append("import org.slf4j.Logger;\n");
    router.append("import org.slf4j.LoggerFactory;\n");
    router.append("\n");
    router.append("public abstract class DevBoxRouter {\n");
    router.append("  private static final Logger ACCESS_LOG = LoggerFactory.getLogger(\"access\");\n");
    router.append("  private static final JsonLogger DEV_ACCESS_LOG = (item) -> ACCESS_LOG.debug(item.toString());\n");
    router.append("\n");

    for (Method method : methods) {
      router.append("  public abstract void handle_").append(method.camelName).append("(long requestId, ");
      for (ParameterDefinition pd : method.parameters) {
        router.append(pd.type.javaType()).append(" ").append(pd.camelName).append(", ");
      }
      router.append(method.responder.camelName).append("Responder responder);\n\n");
    }

    router.append("  public void route(JsonRequest request, JsonResponder responder) {\n");
    router.append("    try {\n");
    router.append("      long requestId = request.id();\n");
    router.append("      String method = request.method();\n");
    router.append("      ObjectNode _accessLogItem = Json.newJsonObject();\n");
    router.append("      _accessLogItem.put(\"method\", method);\n");
    router.append("      _accessLogItem.put(\"requestId\", requestId);\n");
    router.append("      _accessLogItem.put(\"@timestamp\", LogTimestamp.now());\n");
    router.append("      request.dumpIntoLog(_accessLogItem);\n");
    router.append("      switch (method) {\n");
    for (Method method : methods) {
      router.append("        case \"").append(method.name).append("\":\n");
      for (ParameterDefinition pd : method.parameters) {
        if (pd.logged) {
          switch (pd.type) {
            case String:
              if (pd.normalize) {
                router.append("          _accessLogItem.put(\"").append(pd.name).append("\", request.getStringNormalize(\"").append(pd.name).append("\", ").append(pd.optional ? "false" : "true").append(", ").append(pd.errorCodeIfMissing).append("));\n");
              } else {
                router.append("          _accessLogItem.put(\"").append(pd.name).append("\", request.getString(\"").append(pd.name).append("\", ").append(pd.optional ? "false" : "true").append(", ").append(pd.errorCodeIfMissing).append("));\n");
              }
              break;
            case Integer:
              router.append("          _accessLogItem.put(\"").append(pd.name).append("\", request.getInteger(\"").append(pd.name).append("\", ").append(pd.optional ? "false" : "true").append(", ").append(pd.errorCodeIfMissing).append("));\n");
              break;
            case Long:
              router.append("          _accessLogItem.put(\"").append(pd.name).append("\", request.getLong(\"").append(pd.name).append("\", ").append(pd.optional ? "false" : "true").append(", ").append(pd.errorCodeIfMissing).append("));\n");
              break;
            case Boolean:
              router.append("          _accessLogItem.put(\"").append(pd.name).append("\", request.getBoolean(\"").append(pd.name).append("\", ").append(pd.optional ? "false" : "true").append(", ").append(pd.errorCodeIfMissing).append("));\n");
              break;
            default:
          }
        }
      }

      router.append("          handle_").append(method.camelName).append("(requestId, //\n");
      for (ParameterDefinition pd : method.parameters) {
        router.append("            ");
        switch (pd.type) {
          case String:
            if (pd.normalize) {
              router.append("request.getStringNormalize(\"");
            } else {
              router.append("request.getString(\"");
            }
            break;
          case Integer:
            router.append("request.getInteger(\"");
            break;
          case Long:
            router.append("request.getLong(\"");
            break;
          case JsonObject:
            router.append("request.getObject(\"");
            break;
          case JsonObjectOrArray:
            router.append("request.getJsonNode(\"");
            break;
          case Boolean:
            router.append("request.getBoolean(\"");
            break;
        }
        router.append(pd.name).append("\", ").append(pd.optional ? "false" : "true").append(", ").append(pd.errorCodeIfMissing).append("), //\n");
      }
      router.append("            ");
      router.append("new ").append(method.responder.camelName).append("Responder(new DevProxyResponder(responder, _accessLogItem, DEV_ACCESS_LOG)));\n");
      router.append("          return;\n");
    }
    router.append("      }\n");
    router.append("      responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));\n");
    router.append("    } catch (ErrorCodeException ex) {\n");
    router.append("      responder.error(ex);\n");
    router.append("    }\n");
    router.append("  }\n");
    router.append("}\n");
    return router.toString();
  }
}
