package org.adamalang.translator.codegen;

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
      for (DefineMetric dm : environment.document.metrics.values()) {

      }
      sb.append("return __writer.toString();").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }
}
