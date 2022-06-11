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
  public final Writer writer;
  public final VariablePool pool;
  public final String current;
  public final String name;
  public final Element element;
  public final boolean singleParent;
  public final String parentVariable;
  public final boolean returnVariable;
  public final HashMap<String, Integer> subscriptionCounts;

  private Environment(Writer writer, VariablePool pool, String current, String name, Element element, boolean singleParent, String parentVariable, boolean returnVariable, HashMap<String, Integer> subscriptionCounts) {
    this.writer = writer;
    this.pool = pool;
    this.current = current;
    this.name = name;
    this.element = element;
    this.singleParent = singleParent;
    this.parentVariable = parentVariable;
    this.returnVariable = returnVariable;
    this.subscriptionCounts = subscriptionCounts;
  }

  public void assertSoloParent() {
    if (!singleParent) {
      throw new UnsupportedOperationException("<" + element.tagName() + "> was expecting a single parent");
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
    return new Environment(writer, pool, current, name, element, singleParent, parentVariable, returnVariable, subscriptionCounts);
  }

  public Environment current(String current) {
    return new Environment(writer, pool, current, name, element, singleParent, parentVariable, returnVariable, subscriptionCounts);
  }

  public Environment name(String name) {
    return new Environment(writer, pool, current, name, element, singleParent, parentVariable, returnVariable, subscriptionCounts);
  }

  public Environment element(Element element) {
    return new Environment(writer, pool, current, name, element, inferSingleParent(element), parentVariable, returnVariable, subscriptionCounts);
  }

  public Environment returnVariable(boolean returnVariable) {
    return new Environment(writer, pool, current, name, element, singleParent, parentVariable, returnVariable, subscriptionCounts);
  }

  public Environment resetSubscriptionCounts() {
    HashMap<String, Integer> subscriptionCounts = new HashMap<>();
    return new Environment(writer, pool, current, name, element, singleParent, parentVariable, returnVariable, subscriptionCounts);
  }

  public static Environment fresh() {
    return new Environment(new Writer(), new VariablePool(), null, null, null, false, null, false, new HashMap<>());
  }
}
