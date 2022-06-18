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
  public static void template(Environment env) {
    String parentVar = env.pool.ask();
    String stateVar = env.pool.ask();
    env.writer.tab().append("$.register('").append(env.element.attr("name")).append("', function(").append(parentVar).append(", ").append(stateVar).append(") {").newline().tabUp();
    Base.children(env.stateVar(stateVar).parentVariable(parentVar));
    env.pool.give(parentVar);
    env.pool.give(stateVar);
    env.writer.tabDown().tab().append("});").newline();

    /*
    String rootVar = env.pool.ask();
    env.writer.tab().append("$.register('").append(env.element.attr("name")).append("', function(_tree) {").newline().tabUp();
    // TODO: This is a bit of a mess, so we should combine the root var, the delta tree + path into a variable?
    env.writer.tab().append("var _ = {};").newline();
    env.writer.tab().append("var ").append(rootVar).append(" = $.e('div');").newline();
    Base.children(env.current("_tree.tree").parentVariable(rootVar).resetSubscriptionCounts());
    env.writer.tab().append("_tree.onTreeChange(_);").newline();
    env.writer.tab().append("return ").append(rootVar).append(";").newline();
    env.writer.tabDown().tab().append("});").newline();
    */
  }

  public static void page(Environment env) {
    // TODO: parse a mini-language around the path (similar to Adama's get (have first slash be optional, (/ ( text | $ var : type ))*
    // FOR now, have direct lookups
    env.writer.tab().append("$.page('").append(env.element.attr("uri")).append("', function(vs) {").newline().tabUp();
    String rootVar = env.pool.ask();
    env.writer.tab().append("var ").append(rootVar).append(" = document.body;").newline();
    Base.children(env.parentVariable(rootVar));
    env.pool.give(rootVar);
    // At this point, there is no root var as there is no connection. So, we need at least some kind of typing to help with this.
    env.writer.tabDown().tab().append("});").newline();
  }

  public static String finish(Environment env) {
    env.writer.tabDown().tab().append("}").newline();
    return env.writer.toString();
  }
}
