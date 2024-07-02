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

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineStateTransition;

import java.util.Map;

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
