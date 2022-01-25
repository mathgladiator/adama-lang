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
import org.adamalang.apikit.model.Transform;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class AssembleRequestTypes {

  public static Map<String, String> make(String packageName, Method[] methods) throws Exception {
    TreeMap<String, String> files = new TreeMap<>();
    for (Method method : methods) {
      StringBuilder java = new StringBuilder();
      java.append("package ").append(packageName).append(";\n\n");
      for (String imp : method.imports()) {
        java.append("import ").append(imp).append(";\n");
      }

      java.append("\n");
      java.append("/** ").append(fixDocumentation(method.documentation.trim())).append(" */\n");
      java.append("public class ").append(method.camelName).append("Request {\n");
      for (ParameterDefinition parameter : method.parameters) {
        java.append("  public final ").append(parameter.type.javaType()).append(" ").append(parameter.camelName).append(";\n");
        Transform transform = parameter.getTransform(method.name);
        if (transform != null) {
          java.append("  public final ").append(transform.shortOutputJavaType).append(" ").append(transform.outputName).append(";\n");
        }
      }
      java.append("\n");
      java.append("  public ").append(method.camelName).append("Request(");
      boolean first = true;
      for (ParameterDefinition parameter : method.parameters) {
        if (!first) {
          java.append(", ");
        }
        first = false;
        java.append("final ").append(parameter.type.javaType()).append(" ").append(parameter.camelName);
        Transform transform = parameter.getTransform(method.name);
        if (transform != null) {
          java.append(", final ").append(transform.shortOutputJavaType).append(" ").append(transform.outputName);
        }
      }
      java.append(") {\n");
      int outstandingCallCount = 0;
      for (ParameterDefinition parameter : method.parameters) {
        java.append("    this.").append(parameter.camelName).append(" = ").append(parameter.camelName).append(";\n");
        Transform transform = parameter.getTransform(method.name);
        if (transform != null) {
          outstandingCallCount++;
          java.append("    this.").append(transform.outputName).append(" = ").append(transform.outputName).append(";\n");
        }
      }
      java.append("  }\n");
      java.append("\n");

      java.append("  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<").append(method.camelName).append("Request> callback) {\n");
      java.append("    try {\n");
      if (outstandingCallCount > 0) {
        java.append("      final BulkLatch<").append(method.camelName).append("Request> _latch = new BulkLatch<>(nexus.executor, ").append(outstandingCallCount).append(", callback);\n");
      }
      for (ParameterDefinition parameter : method.parameters) {
        java.append("      final ").append(parameter.type.javaType()).append(" ").append(parameter.camelName).append(" = ");
        switch (parameter.type) {
          case String:
            java.append("request.getString(\"");
            break;
          case Integer:
            java.append("request.getInteger(\"");
            break;
          case Long:
            java.append("request.getLong(\"");
            break;
          case JsonObject:
            java.append("request.getObject(\"");
            break;
          case Boolean:
            java.append("request.getBoolean(\"");
            break;
          default:
            throw new RuntimeException();
        }
        java.append(parameter.name).append("\", ").append(parameter.optional ? "false" : "true").append(", ").append(parameter.errorCodeIfMissing).append(");\n");
        if (parameter.validator != null) {
          java.append("      ").append(parameter.validator.shortServiceName).append(".validate(").append(parameter.camelName).append(");\n");
        }
        Transform transform = parameter.getTransform(method.name);
        if (transform != null) {
          java.append("      final LatchRefCallback<").append(transform.shortOutputJavaType).append("> ").append(transform.outputName).append(" = new LatchRefCallback<>(_latch);\n");
        }
      }
      if (outstandingCallCount > 0) {
        java.append("      _latch.with(() -> ");
      } else {
        java.append("      nexus.executor.execute(() -> {\n");
        java.append("        callback.success(");
      }

      java.append("new ").append(method.camelName).append("Request").append("(");
      first = true;
      for (ParameterDefinition parameter : method.parameters) {
        if (!first) {
          java.append(", ");
        }
        first = false;
        java.append(parameter.camelName);
        Transform transform = parameter.getTransform(method.name);
        if (transform != null) {
          java.append(", ").append(transform.outputName).append(".get()");
        }
      }
      java.append(")");

      if (outstandingCallCount > 0) {
        java.append(");\n");
      } else {
        java.append(");\n");
        java.append("      });\n");
      }

      for (ParameterDefinition parameter : method.parameters) {
        Transform transform = parameter.getTransform(method.name);
        if (transform != null) {
          java.append("      nexus.").append(transform.fieldInputName).append(".execute(nexus.session, ").append(parameter.camelName).append(", ").append(transform.outputName).append(");\n");
        }
      }
      java.append("    } catch (ErrorCodeException ece) {\n");
      java.append("      nexus.executor.execute(() -> {\n");
      java.append("        callback.failure(ece);\n");
      java.append("      });\n");
      java.append("    }\n");
      java.append("  }\n");
      java.append("}\n");
      String filename = method.camelName + "Request.java";
      if (files.containsKey(filename)) {
        throw new Exception("method already defined");
      }
      files.put(filename, java.toString());
    }
    return files;
  }

  private static String fixDocumentation(String documentation) {
    String[] lines = documentation.trim().split(Pattern.quote("\n"));
    boolean first = true;
    StringBuilder output = new StringBuilder();
    for (String ln : lines) {
      if (!first) {
        output.append("\n  * ");
      }
      first = false;
      output.append(ln.trim());
    }

    return output.toString();
  }
}
