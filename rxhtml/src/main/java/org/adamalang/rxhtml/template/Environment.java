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
import org.jsoup.nodes.Element;

import java.util.HashMap;

public class Environment {
  public final Environment parent;
  public final Writer writer;
  public final VariablePool pool;
  public final String current;
  public final Element element;
  public final boolean singleParent;
  public final String parentVariable;
  public final boolean returnVariable;
  public final HashMap<String, Integer> subscriptionCounts;
  public final String formVariable;

  private Environment(Environment parent, Writer writer, VariablePool pool, String current, Element element, boolean singleParent, String parentVariable, boolean returnVariable, HashMap<String, Integer> subscriptionCounts, String formVariable) {
    this.parent = parent;
    this.writer = writer;
    this.pool = pool;
    this.current = current;
    this.element = element;
    this.singleParent = singleParent;
    this.parentVariable = parentVariable;
    this.returnVariable = returnVariable;
    this.subscriptionCounts = subscriptionCounts;
    this.formVariable = formVariable;
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
    if (element.childNodeSize() != 1 || !(element.childNode(0) instanceof org.jsoup.nodes.Element)) {
      throw new UnsupportedOperationException("<" + element.tagName() + "> was expecting a single child");
    }
    return element.child(0);
  }

  private static boolean inferSingleParent(Element element) {
    if (element == null) {
      return false;
    }
    return element.children().size() == 1;
  }

  public Environment parentVariable(String parentVariable) {
    return new Environment(this, writer, pool, current, element, singleParent, parentVariable, returnVariable, subscriptionCounts, formVariable);
  }

  public Environment current(String current) {
    return new Environment(this, writer, pool, current, element, singleParent, parentVariable, returnVariable, subscriptionCounts, formVariable);
  }

  public Environment element(Element element) {
    return new Environment(this, writer, pool, current, element, inferSingleParent(element), parentVariable, returnVariable, subscriptionCounts, formVariable);
  }

  public Environment returnVariable(boolean returnVariable) {
    return new Environment(this, writer, pool, current, element, singleParent, parentVariable, returnVariable, subscriptionCounts, formVariable);
  }

  public Environment resetSubscriptionCounts() {
    HashMap<String, Integer> subscriptionCounts = new HashMap<>();
    return new Environment(this, writer, pool, current, element, singleParent, parentVariable, returnVariable, subscriptionCounts, formVariable);
  }

  public Environment formVariable(String formVariable) {
    return new Environment(this, writer, pool, current, element, singleParent, parentVariable, returnVariable, subscriptionCounts, formVariable);
  }

  public static Environment fresh() {
    Writer writer = new Writer();
    writer.append(" function install($) {").tabUp().newline();
    return new Environment(null, writer, new VariablePool(), null, null, false, null, false, new HashMap<>(), null);
  }
}
