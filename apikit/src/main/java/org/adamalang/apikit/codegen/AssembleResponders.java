package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.FieldDefinition;
import org.adamalang.apikit.model.Method;
import org.adamalang.apikit.model.Responder;
import org.adamalang.apikit.model.Type;

import java.util.HashMap;
import java.util.Map;

public class AssembleResponders {
    public static Map<String, String> make(String packageName, Map<String, Responder> responders) throws Exception {
        HashMap<String, String> files = new HashMap<>();
        for (Responder responder : responders.values()) {
            StringBuilder java = new StringBuilder();
            java.append("package ").append(packageName).append(";\n\n");
            for (String imp : responder.imports()) {
                java.append("import ").append(imp).append(";\n");
            }
            java.append("\n");
            java.append("class ").append(responder.camelName).append("Responder {\n");
            java.append("  public final JsonResponder responder;\n");
            java.append("\n");
            java.append("  public ").append(responder.camelName).append("Responder(JsonResponder responder) {\n");
            java.append("    this.responder = responder;\n");
            java.append("  }\n\n");
            String[] names = responder.stream ? new String[] {"next", "finish"} : new String[] {"complete"};
            for (String mName : names) {
                boolean terminal = !mName.equals("next");

                java.append("  public void ").append(mName).append("(");
                boolean first = true;
                for (FieldDefinition fd : responder.fields) {
                    if (!first) {
                        java.append(", ");
                    }
                    first = false;
                    java.append(fd.type.javaType()).append(" ").append(fd.name);
                }
                java.append(") {\n");
                java.append("    ObjectNode _obj = new JsonMapper().createObjectNode();\n");
                for (FieldDefinition fd : responder.fields) {
                    String ext = "";
                    if (fd.optional) {
                        ext = "  ";
                        java.append("    if (").append(fd.name).append(" != null) {\n");
                    }
                    if (fd.type == Type.JsonObject) {
                        java.append(ext + "    _obj.set(\"").append(fd.name).append("\", ").append(fd.name).append(");\n");
                    } else {
                        java.append(ext + "    _obj.put(\"").append(fd.name).append("\", ").append(fd.name).append(");\n");
                    }
                    if (fd.optional) {
                        java.append("    }\n");
                    }
                }
                if (terminal) {
                    java.append("    responder.finish(_obj.toString());\n");
                } else {
                    java.append("    responder.stream(_obj.toString());\n");
                }
                java.append("  }\n\n");
            }


            if (responder.stream) {
                java.append("  public void finish() {\n");
                java.append("    responder.finish(\"{}\");\n");
                java.append("  }\n\n");
            }
            java.append("  public void error(ErrorCodeException ex) {\n");
            java.append("    responder.error(ex);\n");
            java.append("  }\n");

            java.append("}\n");
            files.put(responder.camelName + "Responder.java", java.toString());
        }
        return files;
    }
}
