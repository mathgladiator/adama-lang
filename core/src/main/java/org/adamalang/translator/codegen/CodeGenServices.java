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
      sb.append("public Service __findService(String __name) { return null; }").writeNewline();
    } else {
      for (Map.Entry<String, DefineService> serviceEntry : environment.document.services.entrySet()) {
        sb.append("protected Service ").append(serviceEntry.getKey()).append(";").writeNewline();
      }
      {
        int countdown = environment.document.services.size();
        sb.append("@Override").writeNewline();
        sb.append("public void __link(ServiceRegistry __registry) {").tabUp().writeNewline();
        for (Map.Entry<String, DefineService> serviceEntry : environment.document.services.entrySet()) {
          sb.append(serviceEntry.getKey()).append(" = __registry.find(\"").append(serviceEntry.getKey()).append("\");");
          countdown--;
          if (countdown == 0) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
        sb.append("}").writeNewline();
      }
      {
        int countdown = environment.document.services.size();
        sb.append("@Override").writeNewline();
        sb.append("public Service __findService(String __name) {").tabUp().writeNewline();
        sb.append("switch (__name) {").tabUp().writeNewline();
        for (Map.Entry<String, DefineService> serviceEntry : environment.document.services.entrySet()) {
          sb.append("case \"").append(serviceEntry.getKey()).append("\":").tabUp().writeNewline();
          sb.append("return ").append(serviceEntry.getKey()).append(";").tabDown();
          countdown--;
          if (countdown == 0) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
        sb.append("}").writeNewline();
        sb.append("return null;").tabDown().writeNewline();
        sb.append("}").writeNewline();
      }
    }
  }
}
