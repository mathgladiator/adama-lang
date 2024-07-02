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
import org.adamalang.translator.tree.types.structures.ReplicationDefinition;

import java.util.ArrayList;

public class CodeGenReplication {
  public static void writeReplicationBind(final StringBuilderWithTabs sb, final Environment environment) {
    ArrayList<ReplicationDefinition> replications = new ArrayList<>(environment.document.root.storage.replications.values());
    if (replications.size() == 0) {
      sb.append("@Override").writeNewline();
      sb.append("public void __bindReplication() {}").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("public void __bindReplication() {").tabUp().writeNewline();
      int countDown = replications.size();
      for (ReplicationDefinition defn : replications) {
        sb.append("RxInvalidate __L_").append(defn.name.text).append(" = __setupReplication(\"").append(defn.name.text).append("\",");
        sb.append(defn.service.text).append(",");
        sb.append("\"").append(defn.method.text).append("\",() -> ");
        defn.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        sb.append(");");
        countDown--;
        if (defn.variablesToWatch.size() == 0) {
          if (countDown == 0) {
            sb.tabDown();
          }
        }
        sb.writeNewline();
        int secondCountDown = defn.variablesToWatch.size();
        for (String depend : defn.variablesToWatch) {
          secondCountDown--;
          sb.append(depend).append(".__subscribe(__L_").append(defn.name.text).append(");");
          if (countDown == 0 && secondCountDown == 0) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
      }
      sb.append("}").writeNewline();
    }
  }
}
