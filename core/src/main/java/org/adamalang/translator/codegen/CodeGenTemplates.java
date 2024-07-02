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
import org.adamalang.translator.tree.definitions.DefineTemplate;

import java.util.Map;

/** generate the code to power templates */
public class CodeGenTemplates {
  public static void writeTemplates(final StringBuilderWithTabs sb, final Environment environment) {
    for (Map.Entry<String, DefineTemplate> entry : environment.document.templates.entrySet()) {
      sb.append("public static final NtTemplate ").append(entry.getKey()).append(" = ");
      entry.getValue().value.writeJava(sb, environment);
      sb.append(";").writeNewline();
    }
  }
}
