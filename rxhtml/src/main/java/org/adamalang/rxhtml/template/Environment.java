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

import org.adamalang.rxhtml.Feedback;
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

  public Element soloChild() {
    Element result = null;
    for (int k = 0; k < element.childNodeSize(); k++) {
      Node node = element.childNode(k);
      if (node instanceof TextNode) {
        TextNode text = (TextNode) node;
        if (!text.text().trim().equalsIgnoreCase("")) {
          throw new UnsupportedOperationException("<" + element.tagName() + "> was expecting a single child element (non-ws text elements not allowed)");
        }
      } else if (node instanceof Comment) {
        // ignore comments
      } else if (node instanceof Element) {
        if (result != null) {
          throw new UnsupportedOperationException("<" + element.tagName() + "> was expecting a single child element");
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
