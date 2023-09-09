/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineMetric;

public class CodeGenMetrics {
  public static void writeMetricsDump(final StringBuilderWithTabs sb, final Environment environment) {
    if (environment.document.metrics.size() == 0) {
      sb.append("@Override").writeNewline();
      sb.append("public String __metrics() { return \"{}\"; }").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("public String __metrics() {").tabUp().writeNewline();
      sb.append("JsonStreamWriter __writer = new JsonStreamWriter();").writeNewline();
      sb.append("__writer.beginObject();").writeNewline();
      for (DefineMetric dm : environment.document.metrics.values()) {
        sb.append("__writer.writeObjectFieldIntro(\"" + dm.nameToken.text + "\");");
        if (environment.rules.IsLong(dm.metricType, true)) {
          sb.append("__writer.writeLong(");
        } else if (environment.rules.IsInteger(dm.metricType, true)) {
          sb.append("__writer.writeInteger(");
        } else {
          sb.append("__writer.writeDouble(");
        }
        dm.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        sb.append(");").writeNewline();
      }
      sb.append("__writer.endObject();").writeNewline();
      sb.append("return __writer.toString();").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }
}
