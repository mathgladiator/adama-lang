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

import java.util.ArrayList;
import java.util.TreeSet;

public class CodeGenViewStateFilter {
  public static void writeViewStateFilter(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("@Override").writeNewline();
    sb.append("public String __getViewStateFilter() {").tabUp().writeNewline();
    ArrayList<String> quotedKeys = new ArrayList<>();
    for (String key : new TreeSet<>(environment.document.viewerType.storage.fields.keySet())) {
      quotedKeys.add("\\\"" + key + "\\\"");
    }
    sb.append("return \"[" + String.join(", ", quotedKeys) + "]\";").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}
