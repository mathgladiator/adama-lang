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
import org.adamalang.translator.tree.definitions.DefineCronTask;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;
import org.adamalang.translator.tree.types.structures.FieldDefinition;

import java.util.TreeSet;

public class CodeGenDebug {
  public static void writeDebugInfo(final StringBuilderWithTabs sb, final Environment environment) {
    TreeSet<String> tables = new TreeSet<>();
    for(FieldDefinition fd : environment.document.root.storage.fieldsByOrder) {
      if (fd.type instanceof TyReactiveTable) {
        tables.add(fd.name);
      }
    }

    if (tables.size() == 0 && environment.document.cronTasks.size() == 0) {
      sb.append("@Override").writeNewline();
      sb.append("public void __debug(JsonStreamWriter __writer) {}").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("public void __debug(JsonStreamWriter __writer) {").tabUp().writeNewline();
      sb.append("__writer.writeObjectFieldIntro(\"tables\");").writeNewline();
      sb.append("__writer.beginObject();").writeNewline();
      for (String tbl : tables) {
        sb.append("__writer.writeObjectFieldIntro(\"").append(tbl).append("\");").writeNewline();
        sb.append(tbl).append(".debug(__writer);").writeNewline();
      }
      sb.append("__writer.endObject();").writeNewline();
      sb.append("__writer.writeObjectFieldIntro(\"cron\");").writeNewline();
      sb.append("__writer.beginObject();").writeNewline();
      for (DefineCronTask dct : environment.document.cronTasks.values()) {
        sb.append("__writer.writeObjectFieldIntro(\"").append(dct.name.text).append("\");").writeNewline();
        sb.append("__writer.writeLong(__").append(dct.name.text).append(".get());").writeNewline();
      }
      sb.append("__writer.endObject();").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }
}
