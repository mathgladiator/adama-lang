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
  }

  public static void page(Environment env) {
    // TODO: parse a mini-language around the path (similar to Adama's get (have first slash be optional, (/ ( text | $ var : type ))*
    // FOR now, have direct lookups
    String stateVar = env.pool.ask();
    String rootVar = env.pool.ask();
    env.writer.tab().append("$.page('").append(env.element.attr("uri")).append("', function(").append(stateVar).append(") {").newline().tabUp();
    env.writer.tab().append("var ").append(rootVar).append(" = document.body;").newline();
    Base.children(env.parentVariable(rootVar).stateVar(stateVar));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(rootVar);
    env.pool.give(stateVar);
  }

  public static String finish(Environment env) {
    env.writer.tabDown().tab().append("}").newline();
    return env.writer.toString();
  }
}
