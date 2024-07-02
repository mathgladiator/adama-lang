/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
