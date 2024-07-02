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
import org.adamalang.translator.tree.definitions.DefineConstructor;

/** generates code the constructors within adama's doc */
public class CodeGenConstructor {
  public static void writeConstructors(final StringBuilderWithTabs sb, final Environment environment) {
    var idx = 0;
    var messageTypeNameToUse = "NtMessageBase";
    for (final DefineConstructor dc : environment.document.constructors) {
      if (dc.unifiedMessageTypeNameToUse != null) {
        messageTypeNameToUse = "RTx" + dc.unifiedMessageTypeNameToUse;
      }
      sb.append("private void __construct").append("_" + idx).append("(CoreRequestContext __context, NtPrincipal __who");
      sb.append(", ").append(messageTypeNameToUse).append(" ");
      sb.append(dc.messageNameToken == null ? "__object" : dc.messageNameToken.text);
      sb.append(") {");
      if (dc.code.statements.size() == 0) {
        sb.append("}").writeNewline();
      } else {
        sb.tabUp().writeNewline();
        dc.code.specialWriteJava(sb, environment, false, true);
        sb.append("}").writeNewline();
      }
      idx++;
    }
    if (idx == 0) {
      sb.append("@Override").writeNewline();
      sb.append("protected void __construct_intern(CoreRequestContext _c, NtMessageBase _m) {}").writeNewline();
      sb.append("@Override").writeNewline();
      sb.append("protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {").tabUp().writeNewline();
      sb.append("__reader.skipValue();").writeNewline();
      sb.append("return NtMessageBase.NULL;").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else {
      sb.append("@Override").writeNewline();
      sb.append("protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {").tabUp().writeNewline();
      if (!messageTypeNameToUse.equals("NtMessageBase")) {
        sb.append("return new ").append(messageTypeNameToUse).append("(__reader);").tabDown().writeNewline();
      } else {
        sb.append("__reader.skipValue();").writeNewline();
        sb.append("return NtMessageBase.NULL;").tabDown().writeNewline();
      }
      sb.append("}").writeNewline();
      sb.append("@Override").writeNewline();
      if (!messageTypeNameToUse.equals("NtMessageBase")) {
        sb.append("protected void __construct_intern(CoreRequestContext __context, NtMessageBase __object_pre_cast) {").tabUp().writeNewline();
        sb.append(messageTypeNameToUse).append(" __object = (").append(messageTypeNameToUse).append(") __object_pre_cast;").writeNewline();
      } else {
        sb.append("protected void __construct_intern(CoreRequestContext __context, NtMessageBase __object) {").tabUp().writeNewline();
      }
      for (var k = 0; k < idx; k++) {
        sb.append("__construct").append("_" + k).append("(__context, __context.who, __object);");
        if (k + 1 >= idx) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }
  }
}
