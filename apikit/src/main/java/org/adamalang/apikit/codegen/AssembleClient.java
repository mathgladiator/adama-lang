/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.ParameterDefinition;
import org.adamalang.apikit.model.Responder;

import java.util.ArrayList;

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

  private static String makeStreamStateMachines(Responder[] responders) {
    return "";
  }

  private static String makeInvoke(Method[] methods) throws Exception {
    StringBuilder ts = new StringBuilder();
    for (Method method : methods) {
      if (method.handler.equals("Root")) {
        ts.append("  async ").append(method.camelName2).append("(");
        boolean append1 = false;
        for (ParameterDefinition parameter : method.parameters) {
          if (append1) {
            ts.append(", ");
          }
          append1 = true;
          ts.append(parameter.camelName).append(": ").append(parameter.type.typescriptType());
        }
        ts.append(") {\n");
        ts.append("    var self = this;\n");
        ts.append("    // var id = self.nextId;\n");
        ts.append("    self.nextId++;\n");
        ts.append("    // var request = {\"method\":\"").append(method.name).append("\", \"id\":id");
        for (ParameterDefinition parameter : method.parameters) {
          ts.append(", ").append("\"").append(parameter.name).append("\":").append(parameter.camelName);
        }
        ts.append("};\n");
        if (method.responder.stream) {

        } else {

        }
        ArrayList<Method> submethods = new ArrayList<>();
        for (Method submethod : methods) {
          if (method.createCamel.equals(submethod.handler)) {
            submethods.add(submethod);
          }
        }
        if (submethods.size() > 0) {
          ts.append("    return {\n");
          boolean wrote = false;
          for (Method submethod : submethods) {
            if (wrote) {
              ts.append(",\n");
            }
            wrote = true;
            ts.append("      ").append(submethod.camelName).append(": function(");
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

            ts.append("){\n");
            ts.append("        // var subId = self.nextId;\n");
            ts.append("        self.nextId++;\n");
            ts.append("        // var request = {\"method\":\"").append(submethod.name).append("\", \"id\":subId, \"").append(submethod.findBy).append("\":id");
            for (ParameterDefinition parameter : submethod.parameters) {
              if (parameter.name.equals(submethod.findBy)) {
                continue;
              }
              ts.append(", ").append("\"").append(parameter.name).append("\":").append(parameter.camelName);
            }
            ts.append("};\n");


            ts.append("      }");
          }
          if (wrote) {
            ts.append("\n");
          }
          ts.append("    };\n");
        }
        ts.append("  }\n");
      } else {

      }
    }
    return ts.toString();
  }
}
