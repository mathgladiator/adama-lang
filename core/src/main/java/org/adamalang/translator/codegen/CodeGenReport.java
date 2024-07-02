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
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;

/** when instrumented, generate a report of the state of the reactive connections */
public class CodeGenReport {
  public static void writeRxReport(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, String... others) {
    if (!environment.state.options.instrumentPerf) {
      sb.append("@Override").writeNewline();
      sb.append("public void __writeRxReport(JsonStreamWriter __writer) { }").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("public void __writeRxReport(JsonStreamWriter __writer) {").tabUp().writeNewline();
      sb.append("__writer.beginObject();").writeNewline();
      for (FieldDefinition fd : storage.fields.values()) {
        sb.append(fd.name).append(".__reportRx(\"").append(fd.name).append("\", __writer);").writeNewline();
      }
      for (String other : others) {
        sb.append(other).append(".__reportRx(\"").append(other).append("\", __writer);").writeNewline();
      }
      for (BubbleDefinition bd : storage.bubbles.values()) {
        sb.append("___").append(bd.nameToken.text).append(".__reportRx(\"").append(bd.nameToken.text).append("\", __writer);").writeNewline();
      }
      sb.append("__writer.endObject();").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }
}
