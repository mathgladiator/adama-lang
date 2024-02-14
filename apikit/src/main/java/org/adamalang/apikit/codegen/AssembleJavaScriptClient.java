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
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.*;

import java.util.ArrayList;

public class AssembleJavaScriptClient {

  public static String injectInvokePlainJs(String client, Method[] methods) throws Exception {
    String beginString = "/**[BEGIN-INVOKE]**/";
    int start = client.indexOf(beginString);
    int end = client.indexOf("/**[END-INVOKE]**/");
    if (start > 0 && end > 0) {
      return client.substring(0, start + beginString.length()) + "\n" + makeInvokePlainJs(methods) + "\n  " + client.substring(end);
    } else {
      throw new Exception("Failed to find insertion points within the client");
    }
  }

  private static String makeInvokePlainJs(Method[] methods) throws Exception {
    StringBuilder js = new StringBuilder();
    for (Method method : methods) {
      if (method.internal) {
        continue;
      }
      if (!method.partOfJavaScriptSDK) {
        continue;
      }
      if (method.handler.startsWith("Root")) {
        js.append("  ").append(method.camelName).append("(");
        boolean append1 = false;
        for (ParameterDefinition parameter : method.parameters) {
          if (append1) {
            js.append(", ");
          }
          append1 = true;
          js.append(parameter.camelName);
        }
        if (append1) {
          js.append(", ");
        }
        js.append("responder) {\n");
        js.append("    var self = this;\n");
        js.append("    var parId = self.__id();\n");
        if (method.responder.stream) {
          js.append("    return self.__execute_stream({\n");
        } else {
          js.append("    return self.__execute_rr({\n");
        }
        js.append("      id: parId,\n");
        js.append("      responder: responder,\n");
        js.append("      request: {\"method\":\"").append(method.name).append("\", \"id\":parId");
        for (ParameterDefinition parameter : method.parameters) {
          js.append(", ").append("\"").append(parameter.name).append("\": ").append(parameter.camelName);
        }
        js.append("}");
        ArrayList<Method> submethods = new ArrayList<>();
        for (Method submethod : methods) {
          if (method.createCamel.equals(submethod.handler)) {
            submethods.add(submethod);
          }
        }
        if (submethods.size() > 0) {
          for (Method submethod : submethods) {
            String subMethodNameToUse = submethod.name;
            if (subMethodNameToUse.contains("/")) {
              subMethodNameToUse = subMethodNameToUse.substring(subMethodNameToUse.indexOf('/') + 1);
            }
            js.append(",\n      ").append(Common.camelize(subMethodNameToUse, true)).append(": function(");
            boolean append2 = false;
            for (ParameterDefinition parameter : submethod.parameters) {
              if (submethod.findBy.equals(parameter.name)) {
                continue;
              }
              if (append2) {
                js.append(", ");
              }
              append2 = true;
              js.append(parameter.camelName);
            }
            if (append2) {
              js.append(", ");
            }
            js.append("subResponder) {\n");
            js.append("        var subId = self.__id();\n");
            js.append("        self.__execute_rr({\n");
            js.append("          id: subId,\n");
            js.append("          responder: subResponder,\n");
            js.append("          request: { method: \"").append(submethod.name).append("\", id: subId, \"").append(submethod.findBy).append("\":parId");
            for (ParameterDefinition parameter : submethod.parameters) {
              if (parameter.name.equals(submethod.findBy)) {
                continue;
              }
              js.append(", ").append("\"").append(parameter.name).append("\": ").append(parameter.camelName);
            }
            js.append("}\n");
            js.append("        });\n");
            js.append("      }");
          }
        }
        js.append("\n    });\n");
        js.append("  }\n");
      }
    }
    return js.toString();
  }
}
