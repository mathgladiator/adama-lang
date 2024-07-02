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

import org.adamalang.apikit.model.*;
import org.adamalang.common.Json;

import java.util.*;
import java.util.regex.Pattern;

/** Assemble a java client to talk to the service(s) */
public class AssembleJavaClient {
  public static Map<String, String> make(String packageName, Map<String, Responder> responders, Method[] methods) throws Exception {
    HashMap<String, String> files = new HashMap<>();

    for (Map.Entry<String, Responder> responderEntry : responders.entrySet()) {
      Responder responder = responderEntry.getValue();
      TreeSet<String> imports = new TreeSet<>();

      StringBuilder responderFile = new StringBuilder();
      responderFile.append("package ").append(packageName).append(";\n\n");
      imports.add("com.fasterxml.jackson.databind.node.ObjectNode");
      imports.add("org.adamalang.common.Json");
      for (FieldDefinition pd : responder.fields) {
        pd.type.dumpImports(imports);
      }
      for (String imp : imports) {
        responderFile.append("import ").append(imp).append(";\n");
      }
      if (imports.size() > 0) {
        responderFile.append("\n");
      }
      responderFile.append("/** generated class for the responder: ").append(responder.name).append(" */\n");
      responderFile.append("public class Client" + responder.camelName + "Response {\n");
      responderFile.append("  public final ObjectNode _original;\n");
      for (FieldDefinition fd : responder.fields) {
        responderFile.append("  public final ").append(fd.type.javaType()).append(" ").append(fd.camelName).append(";\n");
      }
      responderFile.append("\n  public Client").append(responder.camelName).append("Response(ObjectNode response) {\n");
      responderFile.append("    this._original = response;\n");
      for (FieldDefinition fd : responder.fields) {
        responderFile.append("    this.").append(fd.camelName).append(" = Json.").append(fd.type.readerMethod()).append("(response, \"").append(fd.name).append("\");\n");
      }
      responderFile.append("  }\n");
      responderFile.append("  public String toInternalJson() {\n");
      responderFile.append("    ObjectNode _next = Json.newJsonObject();\n");
      for (FieldDefinition fd : responder.fields) {
        responderFile.append("    _next.").append(fd.type.writeMethod()).append("(\"").append(fd.camelName).append("\", ").append(fd.camelName).append(");\n");
      }
      responderFile.append("    return _next.toString();\n");
      responderFile.append("  }\n");
      responderFile.append("}\n");
      files.put("Client"+responder.camelName+"Response.java", responderFile.toString());
    }

    StringBuilder client = new StringBuilder();
    client.append("package ").append(packageName).append(";\n\n");

    client.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
    client.append("import org.adamalang.common.Json;\n");
    client.append("import org.adamalang.common.Callback;\n");
    client.append("import org.adamalang.common.Stream;\n");
    client.append("import org.adamalang.web.client.socket.MultiWebClientRetryPool;\n");
    client.append("import org.adamalang.web.client.socket.WebClientConnection;\n");

    client.append("\npublic class SelfClient {\n");
    client.append("private final MultiWebClientRetryPool pool;\n");
    client.append("  \n");
    client.append("  public SelfClient(MultiWebClientRetryPool pool) {\n");
    client.append("    this.pool = pool;\n");
    client.append("  }\n");

    HashSet<String> handlers = new HashSet<>();

    for (Method method : methods) {
      StringBuilder requestType = new StringBuilder();
      {
        requestType.append("package ").append(packageName).append(";\n\n");
        HashSet<String> imports = new HashSet<>();
        for (ParameterDefinition pd : method.parameters) {
          pd.type.dumpImports(imports);
        }
        for (String imp : imports) {
          requestType.append("import ").append(imp).append(";\n");
        }
        if (imports.size() > 0) {
          requestType.append("\n");
        }
        requestType.append("/** generated request type for ").append(method.name).append(" */\n");
        requestType.append("public class Client").append(method.camelName).append("Request {\n");
        for (ParameterDefinition pd : method.parameters) {
          if (method.findBy != null && method.findBy.equals(pd.name)) {

          } else {
            requestType.append("  public ").append(pd.type.javaType()).append(" ").append(pd.camelName).append(";\n");
          }
        }
        requestType.append("}\n");
        files.put("Client" + method.camelName + "Request.java", requestType.toString());
      }
      if (!method.handler.startsWith("Root")) {
        handlers.add(method.handler);
      }
    }

    buildHandler(client, "Root", methods, false);
    for (String childHandler : handlers) {
      client.append("\n");
      client.append("  public class ").append(childHandler).append("Handler {\n");
      client.append("    public final WebClientConnection _direct;\n");
      client.append("    public final int _id;\n");
      client.append("    \n");
      client.append("    public ").append(childHandler).append("Handler(WebClientConnection _direct, int _id) {\n");
      client.append("      this._direct = _direct;\n");
      client.append("      this._id = _id;\n");
      client.append("    }\n\n");
      StringBuilder childClient = new StringBuilder();
      buildHandler(childClient, childHandler, methods, true);
      client.append("    " + childClient.toString().replaceAll(Pattern.quote("\n"), "\n  ").trim()).append("\n");
      client.append("  }\n");
    }
    client.append("}\n");
    files.put("SelfClient.java", client.toString());
    return files;
  }

  private static void buildHandler(StringBuilder client, String handler, Method[] methods, boolean hasDirectConnection) {
    for (Method method : methods) {
      if (handler.equals(method.handler) || handler.startsWith("Root") && method.handler.startsWith("Root")) {
        client.append("\n");
        client.append("  /** ").append(method.name).append(" */\n");

        String methodName = method.camelName2;
        if (!method.handler.startsWith("Root")) {
          methodName = Common.camelize(method.name.substring(method.name.indexOf('/') + 1), true);
        }

        client.append("  public void ").append(methodName).append("(Client").append(method.camelName).append("Request request");
        if (method.responder.stream) {
          if (method.create != null) {
            client.append(", Callback<").append(method.createCamel).append("Handler> callback");
          }
          client.append(", Stream<Client").append(method.responder.camelName).append("Response> streamback");
        } else {
          client.append(", Callback<Client").append(method.responder.camelName).append("Response> callback");
        }
        client.append(") {\n");
        client.append("    ObjectNode node = Json.newJsonObject();\n");
        client.append("    node.put(\"method\", \"").append(method.name).append("\");\n");
        for (ParameterDefinition pd : method.parameters) {
          if (method.findBy != null && method.findBy.equals(pd.name)) {
            client.append("    node.put(\"").append(pd.name).append("\", _id);\n");
          } else {
            client.append("    node.").append(pd.type.putMethod()).append("(\"").append(pd.name).append("\", request.").append(pd.camelName).append(");\n");
          }
        }
        if (method.responder.stream) {
          if (hasDirectConnection) {
            throw new RuntimeException("Can't have a stream within a stream...");
          } else {
            if (method.create != null) {
              client.append("    pool.requestStream(node, (wcc, id) -> new ").append(method.createCamel).append("Handler(wcc, id), (obj) -> new Client").append(method.responder.camelName).append("Response(obj), callback, streamback);\n");
            } else {
              client.append("    pool.requestStream(node, (obj) -> new Client").append(method.responder.camelName).append("Response(obj), streamback);\n");
            }
          }
        } else {
          if (hasDirectConnection) {
            client.append("    _direct.requestResponse(node, (obj) -> new Client").append(method.responder.camelName).append("Response(obj), callback);\n");
          } else {
            client.append("    pool.requestResponse(node, (obj) -> new Client").append(method.responder.camelName).append("Response(obj), callback);\n");
          }
        }
        client.append("  }\n");
      }
    }
  }
}
