/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.ParameterDefinition;
import org.adamalang.apikit.model.Transform;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class AssembleNexus {
  public static String make(String packageName, String prefix, Map<String, ParameterDefinition> parameters) {
    TreeMap<String, Transform> services = new TreeMap<>();
    for (Map.Entry<String, ParameterDefinition> entry : parameters.entrySet()) {
      if (entry.getValue().transform != null) {
        String limitTo = entry.getValue().limitToPrefix;
        if (!limitTo.equals(prefix)) {
          services.put(entry.getKey(), entry.getValue().transform);
        }
      }
    }

    TreeSet<String> imps = new TreeSet<>();
    for (Transform service : services.values()) {
      imps.add(service.service);
    }
    imps.add("org.adamalang.common.SimpleExecutor");
    imps.add("org.adamalang.web.io.JsonLogger;");

    StringBuilder nexus = new StringBuilder();
    nexus.append("package ").append(packageName).append(";\n\n");
    for (String imp : imps) {
      nexus.append("import ").append(imp).append(";\n");
    }
    nexus.append("\n");
    nexus.append("public class ").append(prefix).append("ConnectionNexus {\n");
    nexus.append("  public final JsonLogger logger;\n");
    nexus.append("  public final ").append(prefix).append("ApiMetrics metrics;\n");
    nexus.append("  public final SimpleExecutor executor;\n");
    for (Transform service : services.values()) {
      nexus.append("  public final ").append(service.shortServiceName).append(" ").append(service.fieldInputName).append(";\n");
    }
    nexus.append("\n");
    nexus.append("  public ").append(prefix).append("ConnectionNexus(JsonLogger logger, ").append(prefix).append("ApiMetrics metrics, SimpleExecutor executor");
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
