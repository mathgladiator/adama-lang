/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.web.UriTable;

import java.util.Map;
import java.util.TreeMap;

public class CodeGenWeb {
  private final Environment environment;
  private final UriTable table;
  private final TreeMap<String, String> translate;

  public CodeGenWeb(Environment environment, UriTable table) {
    this.environment = environment;
    this.table = table;
    this.translate = new TreeMap<>();
  }

  private void levelChild(final StringBuilderWithTabs sb, TreeMap<String, UriTable.UriLevel> next, int at, String fieldRaw) {
    for (Map.Entry<String, UriTable.UriLevel> entry : next.entrySet()) {
      final String fieldToUse;
      if (fieldRaw.equals("fragment") && entry.getValue().tail) {
        fieldToUse = "tail()";
      } else {
        fieldToUse = fieldRaw;
      }
      sb.append("if (_").append("" + at).append(".").append(fieldToUse).append(" != null) {").tabUp().writeNewline();
      translate.put(entry.getKey(), "_" + at + "." + fieldToUse);
      level(sb, entry.getValue(), at + 1, true);
      sb.append("}").writeNewline();
    }
  }

  private void level(final StringBuilderWithTabs sb, UriTable.UriLevel level, int at, boolean tabDown) {
    if (!level.check()) {
      return;
    }
    sb.append("WebFragment ").append("_" + at).append(" = __request.router.at(").append("" + at).append(");").writeNewline();
    sb.append("if (_").append("" + at).append(" != null) {").tabUp().writeNewline();
    for (Map.Entry<String, UriTable.UriLevel> entry : level.fixed.entrySet()) {
      sb.append("if (_" + at).append(".fragment.equals(\"").append(entry.getKey()).append("\")) {").tabUp().writeNewline();
      level(sb, entry.getValue(), at + 1, true);
      sb.append("}").writeNewline();
    }
    levelChild(sb, level.bools, at, "val_boolean");
    levelChild(sb, level.ints, at, "val_int");
    levelChild(sb, level.longs, at, "val_long");
    levelChild(sb, level.doubles, at, "val_double");
    levelChild(sb, level.strings, at, "fragment");

    if (level.action != null) {
      sb.append("// EXECUTE ACTION:" + level.name).writeNewline();
    }

    sb.append("/* END:").append("_" + at).append(" */").tabDown().writeNewline();
    sb.append("}");
    if (tabDown) {
      sb.tabDown();
    }
    sb.writeNewline();
  }

  private void table(StringBuilderWithTabs sb) {
    level(sb, this.table.root, 0, false);
  }

  private void handlers(StringBuilderWithTabs sb, Environment environment) {

  }

  public static void writeWebHandlers(final StringBuilderWithTabs sb, Environment environment) {
    sb.append("@Override").writeNewline();
    sb.append("protected WebResponse __get(NtClient __who, WebGet __request) {").tabUp().writeNewline();
    environment.document.webGet.ready("GET");
    CodeGenWeb get = new CodeGenWeb(environment, environment.document.webGet);
    get.table(sb);
    sb.append("return null;").tabDown().writeNewline();
    sb.append("}").writeNewline();
    get.handlers(sb, environment);
  }
}
