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
        sb.append("\"").append(defn.service.text).append("\",");
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
