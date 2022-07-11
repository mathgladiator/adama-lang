package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

public class CodeGenServices {
  public static void writeServices(final StringBuilderWithTabs sb, final Environment environment) {
    if (environment.document.services.size() == 0) {
      sb.append("@Override").writeNewline();
      sb.append("public void __link(ServiceRegistry __registry) {}").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("public void __link(ServiceRegistry __registry) {").tabUp().writeNewline();
      sb.append(" /* not done yet */").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }
}
