/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Function;

public class Base {
  private static final String[] EVENTS = new String[]{"click", "mouseenter", "mouseleave", "load", "success", "failure"};

  private static String xmlnsOf(Environment env) {
    String xmlns = env.element.hasAttr("xmlns") ? env.element.attr("xmlns") : null;
    if (env.element.tagName().equals("svg")) {
      xmlns = "http://www.w3.org/2000/svg";
    }
    if (xmlns == null) {
      xmlns = env.xmlns;
    }
    return xmlns;
  }

  private static boolean testRxCaseIfSoThenOpen(Environment env) {
    boolean hasCase = env.element.hasAttr("rx:case") && env.caseVar != null;
    if (hasCase) {
      env.writer.tab().append("if (").append(env.caseVar).append(" == '").append(env.element.attr("rx:case")).append("') {").tabUp().newline();
      return true;
    }
    return false;
  }

  private static void wrapUpRxCase(Environment env, boolean hasCase) {
    if (hasCase) {
      env.writer.tabDown().tab().append("}").newline();
    }
  }

  private static String writeIntro(Environment env, String xmlns) {
    String eVar = env.pool.ask();
    env.writer.tab().append("var ").append(eVar).append(" = $.E('").append(env.element.tagName()).append("'").append(xmlns != null ? ", '" + xmlns + "'" : "").append(");").newline();
    Attributes rx = new Attributes(env, eVar);
    rx._base();
    if (env.element.tagName().equals("form") && env.element.hasAttr("rx:action")) {
      rx._action();
    }
    for (String event : EVENTS) {
      if (env.element.hasAttr("rx:" + event)) {
        rx._event(event);
      }
    }
    if (env.element.hasAttr("rx:link")) {
      env.writer.tab().append(eVar).append(".link(").append(env.stateVar).append(",'").append(env.element.attr("rx:link")).append("',$);").newline();
    }
    return eVar;
  }

  private static int countAttr(Element element, String... attrs) {
    int count = 0;
    for (String attr : attrs) {
      if (element.hasAttr(attr)) {
        count++;
      }
    }
    return count;
  }

  private static void body(Environment env, String eVar) {
    if (countAttr(env.element, "rx:iterate", "rx:if", "rx:ifnot", "rx:wrap", "rx:switch", "rx:template") > 1) {
      env.feedback.warn(env.element, "Too many incompatible rx:flags");
    }
    Attributes rx = new Attributes(env, eVar);
    if (env.element.hasAttr("rx:iterate")) {
      rx._iterate();
    } else if (env.element.hasAttr("rx:if")) {
      rx._if();
    } else if (env.element.hasAttr("rx:ifnot")) {
      rx._ifnot();
    } else if (env.element.hasAttr("rx:wrap")) {
      rx._wrap();
    } else if (env.element.hasAttr("rx:switch")) {
      rx._switch();
    } else if (env.element.hasAttr("rx:template")) {
      rx._template();
    } else {
      children(env);
    }

  }

  public static String write(Environment env, boolean returnVariable) {
    // get the namespace of the element
    String xmlns = xmlnsOf(env);

    // does the element have an rx:case
    boolean hasCase = testRxCaseIfSoThenOpen(env);

    // introduce the element
    String eVar = writeIntro(env, xmlns);

    // start build a new environment
    Environment next = env.parentVariable(eVar);
    if (xmlns != null) {
      next = next.xmlns(xmlns);
    }
    // apply rx:scope to the environment
    if (env.element.hasAttr("rx:scope")) {
      StatePath path = StatePath.resolve(env.element.attr("rx:scope"), env.stateVar);
      String newStateVar = env.pool.ask();
      env.writer.tab().append("var ").append(newStateVar).append(" = $.pI(").append(path.command).append(",'").append(path.name).append("');").newline();
      next = next.stateVar(newStateVar);
    }

    // write the body of the element (the children and more)
    body(next, eVar);

    // if we have a parent variable, then append it
    if (env.parentVariable != null) {
      env.writer.tab().append(env.parentVariable).append(".append(").append(eVar).append(");").newline();
    }
    wrapUpRxCase(env, hasCase);
    if (returnVariable) {
      return eVar;
    } else {
      env.pool.give(eVar);
      return null;
    }
  }

  private static ArrayList<Node> filtered(Environment env) {
    ArrayList<Node> filtered = new ArrayList<>();
    for (int k = 0; k < env.element.childNodeSize(); k++) {
      Node node = env.element.childNode(k);
      if (node instanceof TextNode) {
        TextNode text = (TextNode) node;
        if (!text.text().trim().equalsIgnoreCase("")) {
          filtered.add(node);
        }
      } else if (node instanceof Comment) {
        // ignore comments
      } else if (node instanceof org.jsoup.nodes.Element) {
        filtered.add(node);
      }
    }
    return filtered;
  }

  public static void children(Environment env) {
    children(env, (x) -> true);
  }

  public static void children(Environment env, Function<Node, Boolean> filter) {
    ArrayList<Node> nodes = filtered(env);
    for (Node node : nodes) {
      if (!filter.apply(node)) {
        continue;
      }
      if (node instanceof TextNode) {
        TextNode text = (TextNode) node;
        env.writer.tab().append(env.parentVariable).append(".append($.T('").append(Escapes.escape39(text.text())).append("'));").newline();
      } else if (node instanceof org.jsoup.nodes.Element) {
        org.jsoup.nodes.Element child = (org.jsoup.nodes.Element) node;
        Environment childEnv = env.element(child, nodes.size() == 1);
        try {
          Method method = Elements.class.getMethod(child.tagName(), Environment.class);
          method.invoke(null, childEnv);
        } catch (Exception ex) {
          Base.write(childEnv, false);
        }
      }
    }
  }
}
