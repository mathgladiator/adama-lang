package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineService;

import java.util.Map;

public class CodeGenServices {
  public static void writeServices(final StringBuilderWithTabs sb, final Environment environment) {
    if (environment.document.services.size() == 0) {
      sb.append("@Override").writeNewline();
      sb.append("public void __link(ServiceRegistry __registry) {}").writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("public void __executeServiceCalls(boolean cancel) {}").writeNewline();
    } else {
      for (Map.Entry<String, DefineService> serviceEntry : environment.document.services.entrySet()) {
        sb.append("protected Service ").append(serviceEntry.getKey()).append(";").writeNewline();
      }
      sb.append("@Override").writeNewline();
      sb.append("public void __link(ServiceRegistry __registry) {").tabUp().writeNewline();
      for (Map.Entry<String, DefineService> serviceEntry : environment.document.services.entrySet()) {
        sb.append(serviceEntry.getKey()).append(" = new ServiceFailure();").writeNewline();
      }
      sb.append(" /* not done yet */").tabDown().writeNewline();
      sb.append("}").writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("public void __executeServiceCalls(boolean cancel) {}").writeNewline();
    }
  }
}
