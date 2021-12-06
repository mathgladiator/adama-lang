package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class AssembleNexus {
    public static String make(String packageName, Map<String, ParameterDefinition> parameters) {
        TreeMap<String, Lookup> services = new TreeMap<>();
        for (Map.Entry<String, ParameterDefinition> entry : parameters.entrySet()) {
            if (entry.getValue().lookup != null) {
                services.put(entry.getKey(), entry.getValue().lookup);
            }
        }

        TreeSet<String> imps = new TreeSet<>();
        for (Lookup service : services.values()) {
            imps.add(service.service);
        }

        StringBuilder nexus = new StringBuilder();
        nexus.append("package ").append(packageName).append(";\n\n");
        for (String imp : imps) {
            nexus.append("import ").append(imp).append(";\n");
        }
        nexus.append("\n");
        nexus.append("class Nexus {\n");

        for (Lookup service : services.values()) {
            nexus.append("  public final ").append(service.shortServiceName).append(" ").append(service.fieldInputName).append(";\n");
        }
        nexus.append("\n");
        nexus.append("  public Nexus(");
        boolean first = true;
        for (Lookup service : services.values()) {
            if (!first) {
                nexus.append(", ");
            }
            first = false;
            nexus.append(service.shortServiceName).append(" ").append(service.fieldInputName);
        }
        nexus.append(") {\n");
        for (Lookup service : services.values()) {
            nexus.append("    this.").append(service.fieldInputName).append(" = ").append(service.fieldInputName).append(";\n");
        }
        nexus.append("  }\n");
        nexus.append("}\n");
        return nexus.toString();
    }

}
