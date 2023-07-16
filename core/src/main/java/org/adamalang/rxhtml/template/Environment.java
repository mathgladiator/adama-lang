/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import org.adamalang.rxhtml.template.config.Feedback;
import org.adamalang.rxhtml.codegen.VariablePool;
import org.adamalang.rxhtml.codegen.Writer;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Environment {
  public final Environment parent;
  public final Feedback feedback;
  public final Writer writer;
  public final VariablePool pool;
  public final Element element;
  public final boolean elementAlone;
  public final String parentVariable;
  public final String stateVar;
  public final String caseVar;
  public final String xmlns;
  public final String fragmentFunc;

  private Environment(Environment parent, Feedback feedback, Writer writer, VariablePool pool, Element element, boolean elementAlone, String parentVariable, String stateVar, String caseVar, String xmlns, String fragmentFunc) {
    this.parent = parent;
    this.feedback = feedback;
    this.writer = writer;
    this.pool = pool;
    this.element = element;
    this.elementAlone = elementAlone;
    this.parentVariable = parentVariable;
    this.stateVar = stateVar;
    this.caseVar = caseVar;
    this.xmlns = xmlns;
    this.fragmentFunc = fragmentFunc;
  }

  public static Environment fresh(Feedback feedback) {
    return new Environment(null, feedback, new Writer(), new VariablePool(), null, false, null, null, null, null, null);
  }

  public String val(String name, String defaultVal) {
    if (element.hasAttr(name)) {
      return element.attr(name);
    }
    return defaultVal;
  }

  public Element soloChildIfPossible() {
    Element result = null;
    for (int k = 0; k < element.childNodeSize(); k++) {
      Node node = element.childNode(k);
      if (node instanceof TextNode) {
        TextNode text = (TextNode) node;
        if (!text.text().trim().equalsIgnoreCase("")) {
          return null;
        }
      } else if (node instanceof Comment) {
        // ignore comments
      } else if (node instanceof Element) {
        if (result != null) {
          return null;
        }
        result = (Element) node;
      }
    }
    return result;
  }

  public Environment element(Element element, boolean elementAlone) {
    return new Environment(this, feedback, writer, pool, element, elementAlone, parentVariable, stateVar, caseVar, xmlns, fragmentFunc);
  }

  public Environment parentVariable(String parentVariable) {
    return new Environment(this, feedback, writer, pool, element, elementAlone, parentVariable, stateVar, caseVar, xmlns, fragmentFunc);
  }

  public Environment stateVar(String stateVar) {
    return new Environment(this, feedback, writer, pool, element, elementAlone, parentVariable, stateVar, caseVar, xmlns, fragmentFunc);
  }

  public Environment caseVar(String caseVar) {
    return new Environment(this, feedback, writer, pool, element, elementAlone, parentVariable, stateVar, caseVar, xmlns, fragmentFunc);
  }

  public Environment xmlns(String xmlns) {
    return new Environment(this, feedback, writer, pool, element, elementAlone, parentVariable, stateVar, caseVar, xmlns, fragmentFunc);
  }

  public Environment fragmentFunc(String fragmentFunc) {
    return new Environment(this, feedback, writer, pool, element, elementAlone, parentVariable, stateVar, caseVar, xmlns, fragmentFunc);
  }
}
