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
import org.adamalang.translator.tree.expressions.Expression;

import java.util.Map;

/** generate the config for the document factory */
public class CodeGenConfig {
  public static void writeConfig(final StringBuilderWithTabs sb, final Environment environment) {
    // join the disconnected handlers into one
    sb.append("public static HashMap<String, Object> __config() {").tabUp().writeNewline();
    sb.append("HashMap<String, Object> __map = new HashMap<>();").writeNewline();
    for (Map.Entry<String, Expression> entry : environment.document.configs.entrySet()) {
      sb.append("__map.put(\"").append(entry.getKey()).append("\", ");
      entry.getValue().writeJava(sb, environment);
      sb.append(");").writeNewline();
    }
    sb.append("return __map;").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}
