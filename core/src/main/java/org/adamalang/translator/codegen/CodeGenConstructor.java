/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineConstructor;

public class CodeGenConstructor {
  public static void writeConstructors(final StringBuilderWithTabs sb, final Environment environment) {
    var idx = 0;
    for (final DefineConstructor dc : environment.document.constructors) {
      sb.append("private void __construct").append("_" + idx).append("(NtClient ");
      sb.append(dc.clientVarToken == null ? "__who" : dc.clientVarToken.text);
      sb.append(", ObjectNode __message) {");
      if (dc.code.statements.size() == 0) {
        sb.append("}").writeNewline();
      } else {
        sb.tabUp().writeNewline();
        if (dc.messageNameToken != null) {
          sb.append("RTx").append(dc.messageTypeToken.text).append(" ").append(dc.messageNameToken.text).append(" = __BRIDGE_").append(dc.messageTypeToken.text).append(".convert(__message);");
        }
        dc.code.specialWriteJava(sb, environment, false, true);
        sb.append("}").writeNewline();
      }
      idx++;
    }
    if (idx == 0) {
      sb.append("@Override").writeNewline();
      sb.append("protected void __construct_intern(NtClient who, ObjectNode message) {}").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("protected void __construct_intern(NtClient who, ObjectNode message) {").tabUp().writeNewline();
      for (var k = 0; k < idx; k++) {
        sb.append("__construct").append("_" + k).append("(who, message);");
        if (k + 1 >= idx) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }
  }
}
