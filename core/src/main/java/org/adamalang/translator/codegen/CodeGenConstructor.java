/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
      sb.append("private void __construct").append("_" + idx).append("(NtPrincipal __who");
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
      sb.append("protected void __construct_intern(NtPrincipal __who, NtMessageBase message) {}").writeNewline();
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
        sb.append("protected void __construct_intern(NtPrincipal __who, NtMessageBase __object_pre_cast) {").tabUp().writeNewline();
        sb.append(messageTypeNameToUse).append(" __object = (").append(messageTypeNameToUse).append(") __object_pre_cast;").writeNewline();
      } else {
        sb.append("protected void __construct_intern(NtPrincipal __who, NtMessageBase __object) {").tabUp().writeNewline();
      }
      for (var k = 0; k < idx; k++) {
        sb.append("__construct").append("_" + k).append("(__who, __object);");
        if (k + 1 >= idx) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }
  }
}
