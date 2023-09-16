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

import org.adamalang.apikit.model.Method;

public class AssembleMetrics {
  public static String make(String packageName, String prefix, Method[] methods) {
    StringBuilder metrics = new StringBuilder();
    metrics.append("package ").append(packageName).append(";\n\n");
    metrics.append("\n");
    metrics.append("import org.adamalang.common.metrics.*;\n");
    metrics.append("\n");
    metrics.append("public class ").append(prefix).append("ApiMetrics {\n");
    for (Method method : methods) {
      if (method.create != null) {
        metrics.append("  public final StreamMonitor monitor_");
      } else {
        metrics.append("  public final RequestResponseMonitor monitor_");
      }
      metrics.append(method.camelName).append(";\n");
    }
    metrics.append("\n");
    metrics.append("  public ").append(prefix).append("ApiMetrics(MetricsFactory factory) {\n");
    for (Method method : methods) {
      metrics.append("    this.monitor_").append(method.camelName);
      if (method.create != null) {
        metrics.append(" = factory.makeStreamMonitor(\"");
      } else {
        metrics.append(" = factory.makeRequestResponseMonitor(\"");
      }
      metrics.append(method.name).append("\");\n");
    }
    metrics.append("  }\n");
    metrics.append("}\n");
    return metrics.toString();
  }
}
