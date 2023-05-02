/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Method;

public class AssembleMetrics {
  public static String make(String packageName, Method[] methods) {
    StringBuilder metrics = new StringBuilder();
    metrics.append("package ").append(packageName).append(";\n\n");
    metrics.append("\n");
    metrics.append("import org.adamalang.common.metrics.*;\n");
    metrics.append("\n");
    metrics.append("public class ApiMetrics {\n");
    for (Method method : methods) {
      if (method.create != null) {
        metrics.append("  public final StreamMonitor monitor_");
      } else {
        metrics.append("  public final RequestResponseMonitor monitor_");
      }
      metrics.append(method.camelName).append(";\n");
    }
    metrics.append("\n");
    metrics.append("  public ApiMetrics(MetricsFactory factory) {\n");
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
