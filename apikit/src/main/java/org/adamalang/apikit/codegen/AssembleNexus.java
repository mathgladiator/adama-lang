/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.ParameterDefinition;
import org.adamalang.apikit.model.Transform;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class AssembleNexus {
  public static String make(String packageName, Map<String, ParameterDefinition> parameters) {
    TreeMap<String, Transform> services = new TreeMap<>();
    for (Map.Entry<String, ParameterDefinition> entry : parameters.entrySet()) {
      if (entry.getValue().transform != null) {
        services.put(entry.getKey(), entry.getValue().transform);
      }
    }

    TreeSet<String> imps = new TreeSet<>();
    for (Transform service : services.values()) {
      imps.add(service.service);
    }
    imps.add("org.adamalang.connection.Session");
    imps.add("org.adamalang.common.SimpleExecutor");
    imps.add("org.adamalang.web.io.JsonLogger;");

    StringBuilder nexus = new StringBuilder();
    nexus.append("package ").append(packageName).append(";\n\n");
    for (String imp : imps) {
      nexus.append("import ").append(imp).append(";\n");
    }
    nexus.append("\n");
    nexus.append("public class ConnectionNexus {\n");
    nexus.append("  public final JsonLogger logger;\n");
    nexus.append("  public final ApiMetrics metrics;\n");
    nexus.append("  public final SimpleExecutor executor;\n");
    for (Transform service : services.values()) {
      nexus.append("  public final ").append(service.shortServiceName).append(" ").append(service.fieldInputName).append(";\n");
    }
    nexus.append("\n");
    nexus.append("  public ConnectionNexus(JsonLogger logger, ApiMetrics metrics, SimpleExecutor executor");
    for (Transform service : services.values()) {
      nexus.append(", ").append(service.shortServiceName).append(" ").append(service.fieldInputName);
    }
    nexus.append(") {\n");
    nexus.append("    this.logger = logger;\n");
    nexus.append("    this.metrics = metrics;\n");
    nexus.append("    this.executor = executor;\n");
    for (Transform service : services.values()) {
      nexus.append("    this.").append(service.fieldInputName).append(" = ").append(service.fieldInputName).append(";\n");
    }
    nexus.append("  }\n");
    nexus.append("}\n");
    return nexus.toString();
  }
}
