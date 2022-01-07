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

public class AssembleMetrics {
  public static String make(String packageName, Method[] methods) {
    StringBuilder metrics = new StringBuilder();
    metrics.append("package ").append(packageName).append(";\n\n");
    metrics.append("\n");
    metrics.append("import org.adamalang.common.metrics.*;\n");
    metrics.append("\n");
    metrics.append("public class Metrics {\n");
    for (Method method : methods) {
      if (method.create != null) {
        metrics.append("  public final StreamMonitor monitor_");
      } else {
        metrics.append("  public final RequestResponseMonitor monitor_");
      }
      metrics.append(method.camelName).append(";\n");
    }
    metrics.append("\n");
    metrics.append("  public Metrics(MetricsFactory factory) {\n");
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
