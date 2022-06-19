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

import org.adamalang.rxhtml.codegen.VariablePool;
import org.adamalang.rxhtml.codegen.Writer;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class Environment {
  public final Environment parent;
  public final Writer writer;
  public final VariablePool pool;
  public final Element element;
  public final boolean singleParent;
  public final String parentVariable;
  public final String formVariable;
  public final String stateVar;
  public final String caseVar;
  public final String xmlns;

  private Environment(Environment parent, Writer writer, VariablePool pool, Element element, boolean singleParent, String parentVariable, String formVariable, String stateVar, String caseVar, String xmlns) {
    this.parent = parent;
    this.writer = writer;
    this.pool = pool;
    this.element = element;
    this.singleParent = singleParent;
    this.parentVariable = parentVariable;
    this.formVariable = formVariable;
    this.stateVar = stateVar;
    this.caseVar = caseVar;
    this.xmlns = xmlns;
  }

  public static Environment fresh() {
    Writer writer = new Writer();
    writer.append(" function install($) {").tabUp().newline();
    return new Environment(null, writer, new VariablePool(), null, false, null, null, null, null, null);
  }

  public void assertSoloParent() {
    if (!singleParent) {
      throw new UnsupportedOperationException("<" + element.tagName() + "> was expecting a single parent");
    }
  }

  public void assertHasParent() {
    if (parentVariable == null) {
      throw new UnsupportedOperationException("<" + element.tagName() + "> must have a valid parent");
    }
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

  public Environment element(Element element) {
    return new Environment(this, writer, pool, element, inferSingleParent(element), parentVariable, formVariable, stateVar, caseVar, xmlns);
  }

  private static boolean inferSingleParent(Element element) {
    if (element == null) {
      return false;
    }
    return element.children().size() == 1;
  }

  public Environment parentVariable(String parentVariable) {
    return new Environment(this, writer, pool, element, singleParent, parentVariable, formVariable, stateVar, caseVar, xmlns);
  }
  public Environment formVariable(String formVariable) {
    return new Environment(this, writer, pool, element, singleParent, parentVariable, formVariable, stateVar, caseVar, xmlns);
  }
  public Environment stateVar(String stateVar) {
    return new Environment(this, writer, pool, element, singleParent, parentVariable, formVariable, stateVar, caseVar, xmlns);
  }
  public Environment caseVar(String caseVar) {
    return new Environment(this, writer, pool, element, singleParent, parentVariable, formVariable, stateVar, caseVar, xmlns);
  }
  public Environment xmlns(String xmlns) {
    return new Environment(this, writer, pool, element, singleParent, parentVariable, formVariable, stateVar, caseVar, xmlns);
  }
}
