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

import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.web.UriAction;
import org.adamalang.translator.tree.definitions.web.UriTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CodeGenWeb {
  private static void writeRouterDiveInto(final StringBuilderWithTabs sb, UriTable.UriLevel level, HashMap<String, UriTable.UriLevel> next, String condition) {
    if (next.size() > 0) {

    }
  }
  private static void writeRouter(final StringBuilderWithTabs sb, UriTable.UriLevel level) {
    writeRouterDiveInto(sb, level, level.fixed, "");


    /**
     *     public final HashMap<String, UriLevel> fixed;
     *     public final HashMap<String, UriLevel> bools;
     *     public final HashMap<String, UriLevel> ints;
     *     public final HashMap<String, UriLevel> longs;
     *     public final HashMap<String, UriLevel> doubles;
     *     public final HashMap<String, UriLevel> strings;
     *     public UriAction action;
     */
  }
  public static void writeWebHandlers(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("@Override").writeNewline();
    sb.append("protected WebResponse __get(NtClient __who, WebGet __request) {").tabUp().writeNewline();
    if (environment.document.webGet.size() > 0) {
      sb.append("WebRouter router = new WebRouter(__request.uri);").writeNewline();
    }


    sb.append("return null;").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}
