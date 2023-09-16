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
