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

public class RxElements {
  public static void template(Environment env) { /* no-op */ }
  public static void page(Environment env) { /* no-op */ }
  public static void fragment(Environment env) {
    env.writer.tab().append(env.fragmentFunc).append("(").append(env.parentVariable).append(",").append(env.stateVar).append(");").newline();
  }
  public static void button(Environment env) {
    String inputVar = Base.write(env, true);
  }
  public static void lookup(Environment env) {
    StatePath path = StatePath.resolve(env.element.attr("path"), env.stateVar);
    // TODO: assert static path
    String transform = env.element.attr("transform");
    if (transform == null || "".equals(transform)) {
      env.writer.tab().append(env.parentVariable).append(".append($.L(").append(path.command).append(", '").append(path.name).append("'));").newline();
    } else {
      // TODO sort out transforms
    }
  }
  public static void connection(Environment env) {
    RxAttributeObject obj = new RxAttributeObject(env, "name", "space", "key", "identity");
    env.writer.tab().append("$.CONNECT(").append(env.stateVar).append(",").append(obj.rxObj).append(");").newline();
    obj.finish();
    if (env.element.childNodeSize() > 0) {
      RxElements.pick(env);
    }
  }
  public static void pick(Environment env) {
    RxAttributeObject obj = new RxAttributeObject(env, "name");
    String sVar = env.pool.ask();
    final String parentVar;
    if (env.elementAlone) {
      parentVar = env.parentVariable;
    } else {
      parentVar = env.pool.ask();
      env.writer.tab().append("var ").append(parentVar).append("=$.E('div');").newline();
      env.writer.tab().append(env.parentVariable).append(".append(").append(parentVar).append(");").newline();
    }
    env.writer.tab().append("$.P(").append(parentVar).append(",").append(env.stateVar).append(",").append(obj.rxObj).append(",function(").append(sVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(sVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    obj.finish();
    env.pool.give(sVar);
  }

  private static String getSyncFunction(String type) {
    if ("submit".equals(type)||"radio".equals(type)) {
      return null;
    }
    if ("checkbox".equals(type)) {
      return "syC";
    }
    if ("radio".equals(type)) {
      return "syR";
    }
    return "syT";
  }

  public static void input(Environment env) {
    String inputVar = Base.write(env, true);
    env.pool.give(inputVar);
  }

  /**
   *
   <input type="button">
   <input type="checkbox">
   <input type="color">
   <input type="date">
   <input type="datetime-local">
   <input type="email">
   <input type="file">
   <input type="hidden">
   <input type="image">
   <input type="month">
   <input type="number">
   <input type="password">
   <input type="radio">
   <input type="range">
   <input type="reset">
   <input type="search">
   <input type="submit">
   <input type="tel">
   <input type="text">
   <input type="time">
   <input type="url">
   <input type="week">

   */
}
