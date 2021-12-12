package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.*;

import java.util.Map;
import java.util.TreeMap;

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
            java.append("class ").append(method.camelName).append("Request {\n");
            for (ParameterDefinition parameter : method.parameters) {
                java.append("  public final ").append(parameter.type.javaType()).append(" ").append(parameter.camelName).append(";\n");
                if (parameter.lookup != null) {
                    java.append("  public final ").append(parameter.lookup.shortOutputJavaType).append(" ").append(parameter.lookup.outputName).append(";\n");
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
                if (parameter.lookup != null) {
                    java.append(", final ").append(parameter.lookup.shortOutputJavaType).append(" ").append(parameter.lookup.outputName);
                }
            }
            java.append(") {\n");
            int outstandingCallCount = 0;
            for (ParameterDefinition parameter : method.parameters) {
                java.append("    this.").append(parameter.camelName).append(" = ").append(parameter.camelName).append(";\n");
                if (parameter.lookup != null) {
                    outstandingCallCount++;
                    java.append("    this.").append(parameter.lookup.outputName).append(" = ").append(parameter.lookup.outputName).append(";\n");
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
                    default:
                        throw new RuntimeException();
                }
                java.append(parameter.name).append("\", ").append(parameter.optional ? "false" : "true").append(", ").append(parameter.errorCodeIfMissing).append(");\n");
                if (parameter.lookup != null) {
                    java.append("      final LatchRefCallback<").append(parameter.lookup.shortOutputJavaType).append("> ").append(parameter.lookup.outputName).append(" = new LatchRefCallback<>(_latch);\n");
                }
                if (parameter.validator != null) {
                    java.append("      ").append(parameter.validator.shortServiceName).append(".validate(").append(parameter.camelName).append(", ").append(parameter.validator.errorCode).append(");\n");
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
                if (parameter.lookup != null) {
                    java.append(", ").append(parameter.lookup.outputName).append(".get()");
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
                if (parameter.lookup != null) {
                    java.append("      nexus.").append(parameter.lookup.fieldInputName).append(".execute(").append(parameter.camelName).append(", ").append(parameter.lookup.outputName).append(");\n");
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
}
