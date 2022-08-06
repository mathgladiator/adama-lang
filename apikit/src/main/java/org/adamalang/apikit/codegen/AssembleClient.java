/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class AssembleClient {

  public static String injectInvoke(String client, Method[] methods) throws Exception {
    String beginString = "/**[BEGIN-INVOKE]**/";
    int start = client.indexOf(beginString);
    int end = client.indexOf("/**[END-INVOKE]**/");
    if (start > 0 && end > 0) {
      return client.substring(0, start + beginString.length()) + "\n" + makeInvoke(methods) + "\n  " + client.substring(end);
    } else {
      throw new Exception("Failed to find insertion points within the client");
    }
  }

  public static String injectResponders(String client, Map<String, Responder> responders) throws Exception{
    String beginString = "/**[BEGIN-EXPORTS]**/";
    int start = client.indexOf(beginString);
    int end = client.indexOf("/**[END-EXPORTS]**/");
    if (start > 0 && end > 0) {
      StringBuilder exports = new StringBuilder();
      exports.append(makeResponders(responders));
      return client.substring(0, start + beginString.length()) + "\n" + exports + "\n  " + client.substring(end);
    } else {
      throw new Exception("Failed to find insertion points within the client");
    }
  }

  private static String makeResponders(Map<String, Responder> responders) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Responder> entry : responders.entrySet()) {
      String name = Common.camelize(entry.getKey());
      Responder responder = entry.getValue();
      sb.append("export interface ").append(name).append("Payload {\n");
      for (FieldDefinition fd : responder.fields) {
        sb.append("  ").append(fd.camelName).append(": ").append(fd.type.typescriptType()).append(";\n");
      }
      sb.append("}\n\n");
      sb.append("export interface ").append(name).append("Responder {\n");
      if (responder.stream) {
        sb.append("  next(data: ").append(name).append("Payload): void;\n");
        sb.append("  complete():  void;\n");
        sb.append("  failure(reason: number): void;\n");
      } else {
        sb.append("  success(data: ").append(name).append("Payload): void;\n");
        sb.append("  failure(reason: number): void;\n");
      }
      sb.append("}\n\n");
    }
    return sb.toString();
  }

  private static String makeInvoke(Method[] methods) throws Exception {
    StringBuilder ts = new StringBuilder();
    for (Method method : methods) {
      if (method.handler.equals("Root")) {
        ts.append("  ").append(method.camelName).append("(");
        boolean append1 = false;
        for (ParameterDefinition parameter : method.parameters) {
          if (append1) {
            ts.append(", ");
          }
          append1 = true;
          ts.append(parameter.camelName).append(": ").append(parameter.type.typescriptType());
        }
        if (append1) {
          ts.append(", ");
        }
        ts.append("responder: ").append(method.responder.camelName).append("Responder) {\n");
        ts.append("    var self = this;\n");
        ts.append("    self.nextId++;\n");
        ts.append("    var parId = self.nextId;\n");
        if (method.responder.stream) {
          ts.append("    return self.__execute_stream({\n");
        } else {
          ts.append("    return self.__execute_rr({\n");
        }
        ts.append("      id: parId,\n");
        ts.append("      responder: responder,\n");
        ts.append("      request: {\"method\":\"").append(method.name).append("\", \"id\":parId");
        for (ParameterDefinition parameter : method.parameters) {
          ts.append(", ").append("\"").append(parameter.name).append("\": ").append(parameter.camelName);
        }
        ts.append("}");
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
            ts.append(",\n      ").append(Common.camelize(subMethodNameToUse, true)).append(": function(");
            boolean append2 = false;
            for (ParameterDefinition parameter : submethod.parameters) {
              if (submethod.findBy.equals(parameter.name)) {
                continue;
              }
              if (append2) {
                ts.append(", ");
              }
              append2 = true;
              ts.append(parameter.camelName).append(": ").append(parameter.type.typescriptType());
            }
            if (append2) {
              ts.append(", ");
            }
            ts.append("subResponder: ").append(submethod.responder.camelName).append("Responder) {\n");
            ts.append("        self.nextId++;\n");
            ts.append("        var subId = self.nextId;\n");
            ts.append("        self.__execute_rr({\n");
            ts.append("          id: subId,\n");
            ts.append("          responder: subResponder,\n");
            ts.append("          request: { method: \"").append(submethod.name).append("\", id: subId, \"").append(submethod.findBy).append("\":parId");
            for (ParameterDefinition parameter : submethod.parameters) {
              if (parameter.name.equals(submethod.findBy)) {
                continue;
              }
              ts.append(", ").append("\"").append(parameter.name).append("\": ").append(parameter.camelName);
            }
            ts.append("}\n");
            ts.append("        });\n");
            ts.append("      }");
          }
        }
        ts.append("\n    });\n");
        ts.append("  }\n");
      }
    }
    return ts.toString();
  }
}
