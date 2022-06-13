/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml.template;

public class Root {
  public static void write(Environment env) {
    String rootVar = env.pool.ask();
    env.writer.tab().append("$.register('").append(env.element.attr("name")).append("', function(_tree) {").newline().tabUp();
    env.writer.tab().append("var _ = {};").newline();
    env.writer.tab().append("var ").append(rootVar).append(" = $.e('div');").newline();
    Base.children(env.current("_tree.tree").parentVariable(rootVar).resetSubscriptionCounts());
    env.writer.tab().append("_tree.onTreeChange(_);").newline();
    env.writer.tab().append("return ").append(rootVar).append(";").newline();
    env.writer.tabDown().tab().append("});").newline();
  }

  public static String finish(Environment env) {
    env.writer.tabDown().tab().append("}").newline();
    return env.writer.toString();
  }
}
