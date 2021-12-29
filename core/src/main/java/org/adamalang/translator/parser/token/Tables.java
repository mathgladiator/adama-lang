/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.parser.token;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** responsible for character tables which indicate membership within a regex */
public class Tables {
  public static final int BOOLEAN_TABLES_SIZE = 256;
  public static final boolean[] DIGITS_SCANNER = assembleBooleanTable("0123456789");
  public static final boolean[] DOUBLE_SCANNER = assembleBooleanTable("0123456789.eE");
  public static final boolean[] HEX_SCANNER = assembleBooleanTable("0123456789xabcdefABCDEF");
  /** used to promote identifiers into keywords which are reserved */
  public static final Set<String> KEYWORD_TABLE = buildKeywordTable();
  public static final boolean[] PART_IDENTIFIER_SCANNER = assembleBooleanTable("qwertyuiopasdfghjklzxcvbnm_QWERTYUIOPASDFGHJKLZXCVBNM0123456789");
  public static final boolean[] SINGLE_CHAR_ESCAPE_SCANNER = assembleBooleanTable("btnrf\"\\");
  public static final boolean[] START_IDENTIFIER_SCANNER = assembleBooleanTable("@#qwertyuiopasdfghjklzxcvbnm_QWERTYUIOPASDFGHJKLZXCVBNM");
  /** various scanner tables */
  public static final boolean[] SYMBOL_SCANNER = assembleBooleanTable("~!$%^&*()-=+[]{}|;:.,?/<>");
  public static final boolean[] WHITESPACE_SCANNER = assembleBooleanTable(" \t\n\r");

  private static boolean[] assembleBooleanTable(final String str) {
    final var table = new boolean[BOOLEAN_TABLES_SIZE];
    // force the array to be clear
    for (var k = 0; k < table.length; k++) {
      table[k] = false;
    }
    str.chars().forEach(x -> {
      table[x] = true;
    });
    return table;
  }

  /** builds KEYWORD_TABLE (once) */
  private static Set<String> buildKeywordTable() {
    final var keywords = new HashSet<String>();
    keywords.add("for");
    keywords.add("foreach");
    keywords.add("while");
    keywords.add("do");
    keywords.add("if");
    keywords.add("else");
    keywords.add("return");
    keywords.add("break");
    keywords.add("continue");
    keywords.add("abort");
    keywords.add("block");
    keywords.add("enum");
    keywords.add("@construct");
    keywords.add("@connected");
    keywords.add("@disconnected");
    keywords.add("@pump");
    keywords.add("@step");
    return Collections.unmodifiableSet(keywords);
  }
}
