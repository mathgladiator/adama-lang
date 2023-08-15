/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;
import org.adamalang.translator.tree.types.structures.FieldDefinition;

import java.util.HashSet;
import java.util.TreeSet;

public class CodeGenDebug {
  public static void writeDebugInfo(final StringBuilderWithTabs sb, final Environment environment) {
    TreeSet<String> tables = new TreeSet<>();
    for(FieldDefinition fd : environment.document.root.storage.fieldsByOrder) {
      if (fd.type instanceof TyReactiveTable) {
        tables.add(fd.name);
      }
    }

    if (tables.size() == 0) {
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
      sb.append("__writer.endObject();").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }
}
