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

import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.template.elements.*;
import org.adamalang.rxhtml.template.elements.defunct.Switch;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Base {

  private static void writeDomSetter(Environment env, String var, String key, String expr) {
    if (key.startsWith("json:")) {
      env.writer.tab().append(var).append(".set_").append(key.substring(5).toLowerCase(Locale.ROOT)).append("(").append(expr).append(");").newline();
    } else {
      env.writer.tab().append(var).append(".setAttribute('").append(key).append("', ").append(expr).append(");").newline();
    }
  }
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
      for (Attribute attr : env.element.attributes().asList()) {
        // String realKey = convertXmlAttributeToJavaScriptField(attr.getKey());
        if (attr.getKey().equals("xmlns") || attr.getKey().startsWith("rx:")) {
          continue;
        }
        if (attr.hasDeclaredValue()) {
          Tree tree = Parser.parse(attr.getValue());
          Map<String, String> vars = tree.variables();
          if (vars.size() > 0) {
            var oVar = env.pool.ask();
            var computeFoo = env.pool.ask();
            env.writer.tab().append("{").tabUp().newline();
            env.writer.tab().append("var ").append(oVar).append(" = {};").newline();
            env.writer.tab().append(oVar).append(".__dom = ").append(eVar).append(";").newline();
            env.writer.tab().append("var ").append(computeFoo).append(" = (function() {").tabUp().newline();
            writeDomSetter(env, "this.__dom", attr.getKey(), tree.js("this"));
            env.writer.tabDown().tab().append("}).bind(").append(oVar).append(");").newline();
            for (Map.Entry<String, String> ve : vars.entrySet()) {
              StatePath path = StatePath.resolve(ve.getValue(), env.stateVar);
              env.writer.tab().append("$.Y(").append(path.command).append(",").append(oVar).append(",'").append(path.name).append("',").append(computeFoo).append(");").newline();
            }
            env.pool.give(oVar);
            env.pool.give(computeFoo);
            env.writer.tab().append(computeFoo).append("();").newline();
            env.writer.tabDown().tab().append("}").newline();
          } else {
            // TODO: escape
            writeDomSetter(env, eVar, attr.getKey(), "'" + attr.getValue() + "'");
          }
        } else {
          writeDomSetter(env, eVar, attr.getKey(), "true");
        }
      }

      Environment next = env.parentVariable(eVar);
      if (xmlns != null) {
        next = next.xmlns(xmlns);
      }
      if (env.element.tagName().equals("form")) {
        next = next.formVariable(eVar);
      }

      RxAttributes rx = new RxAttributes(env, eVar);

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

  public static void children(Environment env) {
    for (int k = 0; k < env.element.childNodeSize(); k++) {
      Node node = env.element.childNode(k);
      if (node instanceof TextNode) {
        TextNode text = (TextNode) node;
        if (!text.text().trim().equalsIgnoreCase("")) {
          env.writer.tab().append(env.parentVariable).append(".append($.t('").append(text.text()).append("'));").newline();
        }
      } else if (node instanceof Comment) {
        // ignore comments
      } else if (node instanceof org.jsoup.nodes.Element) {
        org.jsoup.nodes.Element child = (org.jsoup.nodes.Element) node;
        Environment childEnv = env.element(child);
        switch (child.tagName()) {
          case "template":
          case "page":
            // these handled at a higher level
            break;
          case "connection":
            Connection.write(childEnv);
            break;
          case "decide":
            Decide.write(childEnv);
            break;
          case "execute":
            Execute.write(childEnv);
            break;
          case "input":
            Input.write(childEnv);
            break;
          /*
          case "iterate":
            Iterate.write(childEnv);
            break; */
          case "lookup":
            Lookup.write(childEnv);
            break;
          case "message":
            Message.write(childEnv);
            break;
          case "pick":
            Pick.write(childEnv);
            break;
            /*
          case "scope":
            Scope.write(childEnv);
            break;
            */
            /*
          case "switch":
            Switch.write(childEnv);
            break;
            */
          case "use":
            Use.write(childEnv);
            break;
          default:
            Base.write(childEnv, false);
            break;
        }
      } else {
        throw new UnsupportedOperationException("not sure how to handle node type:" + node.getClass());
      }
    }
  }
}
