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
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.Set;

public class Base {
  private static String convertXmlAttributeToJavaScriptField(String realKey) {
    // TODO: expand mapping of attribute names
    switch (realKey) {
      case "class":
        return "className";
      case "aria-hidden":
        return "ariaHidden";
      case "fill-rule":
        return "fillRule";
      case "clip-rule":
        return "clipRule";
      case "for":
        return "htmlFor";
    }
    return realKey;
  }

  public static String write(Environment env) {
    String eVar = env.pool.ask();
    env.writer.tab().append("var ").append(eVar).append(" = $.e('").append(env.element.tagName()).append("');").newline();
    for (Attribute attr : env.element.attributes().asList()) {
      String realKey = convertXmlAttributeToJavaScriptField(attr.getKey());

      if (attr.hasDeclaredValue()) {
        Tree tree = Parser.parse(attr.getValue());
        Set<String> vars = tree.variables();

        if (vars.size() > 0) {
          var oVar = env.pool.ask();
          env.writer.tab().append("{").tabUp().newline();
          env.writer.tab().append("var ").append(oVar).append(" = {};").newline();
          // copy
          for (String var : vars) {
            env.writer.tab().append(oVar).append(".").append(var).append(" = ").append(env.current).append(".").append(var).append(";").newline();
          }
          env.writer.tab().append(oVar).append("._ = function() {").tabUp().newline();
          env.writer.tab().append(eVar).append(".").append(realKey).append(" = '").append(tree.js(oVar)).append("';").newline();
          env.writer.tabDown().tab().append("}").newline();
          // TODO: for each variable, subscribe to an update, then fire _()
          // NOTE: handling multiple callbacks per field is a huge bug
          env.writer.tab().append(oVar).append("._();").newline();
          env.writer.tabDown().tab().append("}").newline();
        } else {
          env.writer.tab().append(eVar).append(".").append(realKey).append(" = '").append(attr.getValue()).append("';").newline();
        }
      } else {
        env.writer.tab().append(eVar).append(".").append(realKey).append(" = true;").newline();
      }
    }
    Environment next = env.parentVariable(eVar);
    if (env.element.tagName().equals("form")) {
      next = next.formVariable(eVar);
    }
    children(next);
    if (env.parentVariable != null) {
      env.writer.tab().append(env.parentVariable).append(".append(").append(eVar).append(");").newline();
    }
    if (env.returnVariable) {
      return eVar;
    } else {
      env.pool.give(eVar);
      return null;
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
        Environment childEnv = env.element(child).returnVariable(false);
        switch (child.tagName()) {
          case "template":
          case "page":
            // these handled at a higher level
            break;
          case "authenticate":
            Authenticate.write(childEnv);
            break;
          case "forgot-password":
            ForgotPassword.write(childEnv);
            break;
          case "scope":
            Scope.write(childEnv);
            break;
          case "test":
            Test.write(childEnv);
            break;
          case "iterate":
            Iterate.write(childEnv);
            break;
          case "message":
            Message.write(childEnv);
            break;
          case "execute":
            Execute.write(childEnv);
            break;
          case "input":
            Input.write(childEnv);
            break;
          case "lookup":
            Lookup.write(childEnv);
            break;
          case "use":
            Use.write(childEnv);
            break;
          default:
            Base.write(childEnv);
            break;
        }
      } else {
        throw new UnsupportedOperationException("not sure how to handle node type:" + node.getClass());
      }
    }
  }
}
