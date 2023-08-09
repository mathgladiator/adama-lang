/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import org.adamalang.common.Escaping;
import org.adamalang.common.Hashing;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

public class Elements {
  public static void template(Environment env) { /* no-op */ }

  public static void page(Environment env) { /* no-op */ }

  public static void shell(Environment env) { /* no-op */ }

  public static void style(Environment env) { /* no-op */ }

  public static void script(Environment env) { /* no-op */ }

  public static void meta(Environment env) { /* no-op */ }

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
      env.writer.tab().append(env.parentVariable).append(".append($.LT(").append(path.command).append(",'").append(path.name).append("',$.TR('").append(transform).append("')));").newline();
    }
  }

  public static void todotask(Environment env) {
    String section = env.section;
    String description = env.element.html();
    MessageDigest md5 = Hashing.md5();
    md5.update(section.getBytes(StandardCharsets.UTF_8));
    md5.update(description.getBytes(StandardCharsets.UTF_8));
    String id = Hashing.finishAndEncode(md5);
    env.tasks.add(new Task(id, section, description));
    env.writer.tab().append("$.TASK(").append(env.parentVariable).append(",'").append(id).append("','").append(section).append("',\"").append(new Escaping(description).go()).append("\");\n");
  }

  public static void title(Environment env) {
    RxObject obj = new RxObject(env, "value");
    env.writer.tab().append("$.ST(").append(obj.rxObj).append(");").newline();
  }

  public static void viewsync(Environment env) {
    String childStateVar = env.pool.ask();
    String parentVar = soloParent(env);
    env.writer.tab().append("$.VSy(").append(parentVar).append(",").append(env.stateVar).append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), (node) -> {
      if (node instanceof Element) {
        return !(node.hasAttr("rx:else") || node.hasAttr("rx:disconnected"));
      } else {
        return true;
      }
    });
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), (node) -> {
      if (node instanceof Element) {
        return (node.hasAttr("rx:else") || node.hasAttr("rx:disconnected"));
      } else {
        return false;
      }
    });
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(childStateVar);
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

  public static void connection(Environment env) {
    if (!env.element.hasAttr("name")) {
      env.element.attr("name", "default");
    }
    if (!env.element.hasAttr("redirect")) {
      env.element.attr("redirect", "/sign-in");
    }
    if (env.element.hasAttr("use-domain")) {
      // TODO: test this
      RxObject obj = new RxObject(env, "name", "identity", "redirect");
      env.writer.tab().append("$.DCONNECT(") //
          .append(env.stateVar) //
          .append(",").append(obj.rxObj) //
          .append(");").newline();
      obj.finish();
      if (env.element.childNodeSize() > 0) {
        Elements.pick(env);
      }
    } else {
      // TODO: validate that space and key are set
      RxObject obj = new RxObject(env, "name", "space", "key", "identity", "redirect");
      env.writer.tab().append("$.CONNECT(") //
          .append(env.stateVar) //
          .append(",").append(obj.rxObj) //
          .append(");").newline();
      obj.finish();
      if (env.element.childNodeSize() > 0) {
        Elements.pick(env);
      }
    }
  }

  public static void pick(Environment env) {
    if (!env.element.hasAttr("name")) {
      env.element.attr("name", "default");
    }
    RxObject obj = new RxObject(env, "name");
    String childStateVar = env.pool.ask();
    String parentVar = soloParent(env);
    env.writer.tab().append("$.P(").append(parentVar).append(",").append(env.stateVar).append(",").append(obj.rxObj).append(",function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), (node) -> {
      if (node instanceof Element) {
        return !(node.hasAttr("rx:else") || node.hasAttr("rx:disconnected"));
      } else {
        return true;
      }
    });
    env.writer.tabDown().tab().append("},function(").append(parentVar).append(",").append(childStateVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).parentVariable(parentVar), (node) -> {
      if (node instanceof Element) {
        return (node.hasAttr("rx:else") || node.hasAttr("rx:disconnected"));
      } else {
        return false;
      }
    });
    env.writer.tabDown().tab().append("});").newline();
    obj.finish();
    env.pool.give(childStateVar);
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
        .append(",'").append(env.element.attr("src")).append("',").append(obj.rxObj) //
        .append(",'").append(env.val("redirect", "/sign-in")) //
        .append("',function(").append(sVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(sVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    obj.finish();
    env.pool.give(sVar);
  }

  public static void textarea(Environment env) {
    input(env);
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

  public static void select(Environment env) {
    input(env);
  }
}
