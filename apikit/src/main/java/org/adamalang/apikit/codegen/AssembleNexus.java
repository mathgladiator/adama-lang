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
    imps.add("java.util.concurrent.Executor");

    StringBuilder nexus = new StringBuilder();
    nexus.append("package ").append(packageName).append(";\n\n");
    for (String imp : imps) {
      nexus.append("import ").append(imp).append(";\n");
    }
    nexus.append("\n");
    nexus.append("public class ConnectionNexus {\n");
    nexus.append("  public final Executor executor;\n");
    for (Transform service : services.values()) {
      nexus
          .append("  public final ")
          .append(service.shortServiceName)
          .append(" ")
          .append(service.fieldInputName)
          .append(";\n");
    }
    nexus.append("\n");
    nexus.append("  public ConnectionNexus(Executor executor");
    for (Transform service : services.values()) {
      nexus
          .append(", ")
          .append(service.shortServiceName)
          .append(" ")
          .append(service.fieldInputName);
    }
    nexus.append(") {\n");
    nexus.append("    this.executor = executor;");
    for (Transform service : services.values()) {
      nexus
          .append("    this.")
          .append(service.fieldInputName)
          .append(" = ")
          .append(service.fieldInputName)
          .append(";\n");
    }
    nexus.append("  }\n");
    nexus.append("}\n");
    return nexus.toString();
  }
}
