/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineService;

import java.util.Map;

public class CodeGenServices {
  public static void writeServices(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("public static HashMap<String, HashMap<String, Object>> __services() {").tabUp().writeNewline();
    sb.append("HashMap<String, HashMap<String, Object>> __map = new HashMap<>();").writeNewline();
    for (Map.Entry<String, DefineService> serviceEntry : environment.document.services.entrySet()) {
      sb.append("HashMap<String, Object> ").append(serviceEntry.getKey()).append(" = new HashMap<>();").writeNewline();
      for (DefineService.ServiceAspect aspect : serviceEntry.getValue().aspects) {
        sb.append(serviceEntry.getKey()).append(".put(\"").append(aspect.name.text).append("\", ");
        aspect.expression.writeJava(sb, environment);
        sb.append(");").writeNewline();
      }
      sb.append("__map.put(\"").append(serviceEntry.getKey()).append("\",").append(serviceEntry.getKey()).append(");").writeNewline();
    }
    sb.append("return __map;").tabDown().writeNewline();
    sb.append("}").writeNewline();

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
        sb.append(serviceEntry.getKey()).append(" = __registry.find(\"").append(serviceEntry.getKey()).append("\");").writeNewline();
      }
      sb.append(" /* not done yet */").tabDown().writeNewline();
      sb.append("}").writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("public void __executeServiceCalls(boolean cancel) {}").writeNewline();
    }
  }
}
