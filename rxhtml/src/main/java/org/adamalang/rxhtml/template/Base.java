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

import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Function;

public class Base {
  public static String write(Environment env, boolean returnVariable) {
    String xmlns = env.element.hasAttr("xmlns") ? env.element.attr("xmlns") : null;
    if (env.element.tagName().equals("svg")) {
      xmlns = "http://www.w3.org/2000/svg";
    }
    if (xmlns == null) {
      xmlns = env.xmlns;
    }

    if (env.element.attributesSize() == 0 && env.element.childNodeSize() == 0 && !returnVariable) {
      env.writer.tab().append(env.parentVariable).append(".append(").append("$.E('").append(env.element.tagName()).append("'").append(xmlns != null ? ", '" + xmlns + "'" : "").append("));").newline();
      return null;
    } else {
      String eVar = env.pool.ask();
      boolean hasCase = env.element.hasAttr("rx:case") && env.caseVar != null;
      if (hasCase) {
        env.writer.tab().append("if (").append(env.caseVar).append(" == '").append(env.element.attr("rx:case")).append("') {").tabUp().newline();
      }
      env.writer.tab().append("var ").append(eVar).append(" = $.E('").append(env.element.tagName()).append("'").append(xmlns != null ? ", '" + xmlns + "'" : "").append(");").newline();
      RxAttributes rx = new RxAttributes(env, eVar);
      rx._base();

      Environment next = env.parentVariable(eVar);
      if (xmlns != null) {
        next = next.xmlns(xmlns);
      }
      if (env.element.tagName().equals("form")) {
        next = next.formVariable(eVar);
      }

      // TODO: warning if too many of the rx:*

      if (env.element.hasAttr("rx:iterate")) {
        rx._iterate();
      } else if (env.element.hasAttr("rx:if")) {
        rx._if();
      } else if (env.element.hasAttr("rx:ifnot")) {
        rx._ifnot();
      } else if (env.element.hasAttr("rx:scope")) {
        rx._scope();
      } else if (env.element.hasAttr("rx:switch")) {
        rx._switch();
      } else if (env.element.hasAttr("rx:template")) {
        rx._template();
      } else {
        children(next);
      }

      if (env.parentVariable != null) {
        env.writer.tab().append(env.parentVariable).append(".append(").append(eVar).append(");").newline();
      }
      if (hasCase) {
        env.writer.tabDown().tab().append("}").newline();
      }
      if (returnVariable) {
        return eVar;
      } else {
        env.pool.give(eVar);
        return null;
      }
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
        env.writer.tab().append(env.parentVariable).append(".append($.T('").append(text.text()).append("'));").newline();
      } else if (node instanceof org.jsoup.nodes.Element) {
        org.jsoup.nodes.Element child = (org.jsoup.nodes.Element) node;
        Environment childEnv = env.element(child, nodes.size() == 1);
        try {
          Method method = RxElements.class.getMethod(child.tagName(), Environment.class);
          method.invoke(null, childEnv);
        } catch (IllegalAccessException | InvocationTargetException bad) {
          bad.printStackTrace();
        } catch (NoSuchMethodException nsme) {
          Base.write(childEnv, false);
        }
      } else {
        throw new UnsupportedOperationException("not sure how to handle node type:" + node.getClass());
      }
    }
  }
}
