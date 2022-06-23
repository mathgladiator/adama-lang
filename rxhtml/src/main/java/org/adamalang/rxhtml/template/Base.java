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

import java.util.Map;
import java.util.Set;

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
            env.writer.tab().append("this.__dom.setAttribute('").append(attr.getKey()).append("', ").append(tree.js("this")).append(");").newline();
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
            env.writer.tab().append(eVar).append(".setAttribute('").append(attr.getKey()).append("', '").append(attr.getValue()).append("');").newline();
            // env.writer.tab().append(eVar).append(".").append(realKey).append(" = '").append(attr.getValue()).append("';").newline();
          }
        } else {
          env.writer.tab().append(eVar).append(".setAttribute('").append(attr.getKey()).append("', true);").newline();
          // env.writer.tab().append(eVar).append(".").append(realKey).append(" = true;").newline();
        }
      }



      Environment next = env.parentVariable(eVar);
      if (xmlns != null) {
        next = next.xmlns(xmlns);
      }
      if (env.element.tagName().equals("form")) {
        next = next.formVariable(eVar);
      }

      boolean expand = env.element.hasAttr("rx:expand-view-state");

      if (env.element.hasAttr("rx:iterate")) {
        StatePath path = StatePath.resolve(env.element.attr("rx:iterate"), env.stateVar);
        String childStateVar = env.pool.ask();
        env.writer.tab().append("$.IT(").append(eVar).append(", ").append(path.command).append(", '").append(path.name).append("', function(").append(childStateVar).append(") {").tabUp().newline();
        String childDomVar = Base.write(env.stateVar(childStateVar).parentVariable(null).element(env.soloChild()), true);
        env.writer.tab().append("return ").append(childDomVar).append(";").newline();
        env.pool.give(childDomVar);
        env.writer.tabDown().tab().append("});").newline();
        env.pool.give(childStateVar);
      } else if (env.element.hasAttr("rx:if")) {
        StatePath path = StatePath.resolve(env.element.attr("rx:if"), env.stateVar);
        String childStateVar = env.pool.ask();
        env.writer.tab().append("$.IF(").append(eVar).append(", ").append(path.command);
        env.writer.append(", '").append(path.name).append("', function(").append(childStateVar).append(") {").tabUp().newline();
        Base.children(env.stateVar(childStateVar).parentVariable(eVar));
        env.writer.tabDown().tab().append("});").newline();
        env.pool.give(childStateVar);
      } else if (env.element.hasAttr("rx:ifnot")) {

      } else if (env.element.hasAttr("rx:scope")) {
        StatePath path = StatePath.resolve(env.element.attr("rx:scope"), env.stateVar);
        String newStateVar = env.pool.ask();
        env.writer.tab().append("var ").append(newStateVar).append(" = $.pI(").append(path.command).append(", '").append(path.name).append("');").newline();
        Base.children(env.stateVar(newStateVar));
      } else if (env.element.hasAttr("rx:switch")) {
        StatePath path = StatePath.resolve(env.element.attr("rx:switch"), env.stateVar);
        String childStateVar = env.pool.ask();
        String caseVar = env.pool.ask();
        env.writer.tab().append("$.W(").append(eVar).append(", ").append(path.command);
        env.writer.append(", '").append(path.name).append("', function(").append(childStateVar).append(", ").append(caseVar).append(") {").tabUp().newline();
        Base.children(env.stateVar(childStateVar).caseVar(caseVar).parentVariable(eVar));
        env.writer.tabDown().tab().append("});").newline();
        env.pool.give(caseVar);
        env.pool.give(childStateVar);
      } else {
        children(next);
      }

      if (env.parentVariable != null) {
        if (env.element.hasAttr("rx:if")) {
          // is this like a switch, or is this something else?
        } else if (env.element.hasAttr("rx:ifnot")) {

        } else {
          // default
        }
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
          case "test":
            Test.write(childEnv);
            break;
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
