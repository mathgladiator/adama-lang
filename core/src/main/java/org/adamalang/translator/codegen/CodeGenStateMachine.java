/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.codegen;

import java.util.Map;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineStateTransition;

/** responsible for writing the state machine stepper */
public class CodeGenStateMachine {
  public static void writeStateMachine(final StringBuilderWithTabs sb, final Environment environment) {
    // write the code for each step function
    for (final Map.Entry<String, DefineStateTransition> entry : environment.document.transitions.entrySet()) {
      sb.append("private void __step_" + entry.getKey() + "() ");
      entry.getValue().code.writeJava(sb, environment.scopeAsStateMachineTransition());
      sb.writeNewline();
    }
    var n = environment.document.transitions.size();
    sb.append("@Override").writeNewline();
    if (n > 0) {
      sb.append("protected void __invoke_label(String __new_state) {").tabUp().writeNewline();
      sb.append("switch(__new_state) {").tabUp().writeNewline();
      for (final Map.Entry<String, DefineStateTransition> entry : environment.document.transitions.entrySet()) {
        sb.append(String.format("case \"%s\":", entry.getKey())).tabUp().writeNewline();
        sb.append(String.format("__step_%s();", entry.getKey())).writeNewline();
        sb.append("return;");
        n--;
        if (n == 0) {
          sb.tabDown();
        }
        sb.tabDown().writeNewline();
      }
      sb.append("}").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else {
      sb.append("protected void __invoke_label(String __new_state) {}").writeNewline();
    }
  }
}
