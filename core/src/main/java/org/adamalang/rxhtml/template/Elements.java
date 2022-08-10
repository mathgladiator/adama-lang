/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.template;

import org.jsoup.nodes.Attribute;

import java.util.ArrayList;

public class Elements {
  public static void template(Environment env) { /* no-op */ }

  public static void page(Environment env) { /* no-op */ }

  public static void fragment(Environment env) {
    String caseToUse = "";
    if (env.element.hasAttr("case")) {
      caseToUse = env.element.attr("case");
    }
    env.writer.tab().append(env.fragmentFunc).append("(").append(env.parentVariable).append(",").append(env.stateVar).append(",'").append(caseToUse).append("');").newline();
  }

  public static void lookup(Environment env) {
    StatePath path = StatePath.resolve(env.element.attr("path"), env.stateVar);
    String transform = env.element.attr("transform");
    if (transform == null || "".equals(transform)) {
      env.writer.tab().append(env.parentVariable).append(".append($.L(").append(path.command).append(",'").append(path.name).append("'));").newline();
    } else {
      env.writer.tab().append(env.parentVariable).append(".append($.LT(").append(path.command).append(",'").append(path.name).append("',").append(Transforms.of(transform)).append("));").newline();
    }
  }

  public static void connection(Environment env) {
    if (!env.element.hasAttr("name")) {
      env.element.attr("name", "default");
    }
    // TODO: validate that space and key are set
    RxObject obj = new RxObject(env, "name", "space", "key", "identity");
    env.writer.tab().append("$.CONNECT(") //
        .append(env.stateVar) //
        .append(",").append(obj.rxObj) //
        .append(",'").append(env.val("redirect", "/sign-in")) //
        .append("');").newline();
    obj.finish();
    if (env.element.childNodeSize() > 0) {
      Elements.pick(env);
    }
  }

  private static String soloParent(Environment env) {
    final String parentVar;
    if (env.elementAlone) {
      parentVar = env.parentVariable;
    } else {
      parentVar = env.pool.ask();
      env.writer.tab().append("var ").append(parentVar).append("=$.E('div');").newline();
      env.writer.tab().append(env.parentVariable).append(".append(").append(parentVar).append(");").newline();
    }
    return parentVar;
  }

  public static void customdata(Environment env) {
    ArrayList<String> parameters = new ArrayList<>();
    for (Attribute attr : env.element.attributes()) {
      if (attr.getKey().startsWith("parameter:")) {
        parameters.add(attr.getKey());
      }
    }
    RxObject obj = new RxObject(env, parameters.toArray(new String[parameters.size()]));
    String sVar = env.pool.ask();
    String parentVar = soloParent(env);

    env.writer.tab().append("$.CUDA(") //
        .append(parentVar) //
        .append(",").append(env.stateVar) //
        .append(",'").append(env.element.attr("src"))
        .append("',").append(obj.rxObj) //
        .append(",'").append(env.val("redirect", "/sign-in")) //
        .append("',function(").append(sVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(sVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    obj.finish();
    env.pool.give(sVar);
  }

  public static void pick(Environment env) {
    if (!env.element.hasAttr("name")) {
      env.element.attr("name", "default");
    }
    RxObject obj = new RxObject(env, "name");
    String sVar = env.pool.ask();
    String parentVar = soloParent(env);
    env.writer.tab().append("$.P(").append(parentVar).append(",").append(env.stateVar).append(",").append(obj.rxObj).append(",function(").append(sVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(sVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    obj.finish();
    env.pool.give(sVar);
  }

  public static void input(Environment env) {
    String inputVar = Base.write(env, true);
    if (env.element.hasAttr("rx:sync")) {
      String path = env.element.attr("rx:sync");
      boolean tuned = path.startsWith("view:") | path.startsWith("data:");
      double ms = _rxdebounce(env);
      StatePath _path = StatePath.resolve(tuned ? path : ("view:" + path), env.stateVar);
      env.writer.tab().append("$.SY(").append(inputVar).append(",").append(_path.command).append(",'").append(_path.name).append("',").append("" + ms).append(");").newline();
    }
    env.pool.give(inputVar);
  }

  private static double _rxdebounce(Environment env) {
    if (env.element.hasAttr("rx:debounce")) {
      try {
        return Double.parseDouble(env.element.attr("rx:debounce"));
      } catch (IllegalArgumentException nfe) {
        env.feedback.warn(env.element, env.element.attr("rx:debounce") + " should be a numeric value");
        return 50;
      }
    }
    return 100;
  }

  public static void textarea(Environment env) {
    input(env);
  }

  public static void select(Environment env) {
    input(env);
  }
}
