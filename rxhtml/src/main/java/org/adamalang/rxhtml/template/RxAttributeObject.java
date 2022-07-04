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
import org.jsoup.nodes.Attribute;

import java.util.ArrayList;
import java.util.Map;

public class RxAttributeObject {
  private final Environment env;
  private final ArrayList<Attribute> attributes;
  public final String rxObj;
  public final boolean delayed;
  public RxAttributeObject(Environment env, String... names) {
    this.env = env;
    this.attributes = new ArrayList<>();
    rxObj = env.pool.ask();
    env.writer.tab().append("var ").append(rxObj).append("={};").newline();
    boolean addedUnder = false;
    boolean _delayed = false;
    for (String name : names) {
      if (env.element.hasAttr(name)) {
        String value = env.element.attr(name);
        Tree tree = Parser.parse(value);
        Map<String, String> vars = tree.variables();
        if (vars.size() > 0) {
          if (!addedUnder) {
            env.writer.tab().append(rxObj).append("._={};").newline();
            addedUnder = true;
          }
          env.writer.tab().append(rxObj).append("._.").append(name).append("={};").newline();
          env.writer.tab().append(rxObj).append("._._ = function() {").tabUp().newline();
          env.writer.tab().append(rxObj).append(".").append(name).append("=").append(tree.js(rxObj + "._." + name)).newline();
          env.writer.tab().append(rxObj).append(".__();").tabDown().newline();
          env.writer.tab().append("}").newline();
          for (Map.Entry<String, String> ve : vars.entrySet()) {
            StatePath path = StatePath.resolve(ve.getValue(), env.stateVar);
            env.writer.tab().append("$.Y(").append(path.command).append(",").append(rxObj).append("._.").append(name).append(",'").append(path.name).append("', ").append(rxObj).append("._._").append(");").newline();
          }
          _delayed = true;
        } else {
          env.writer.tab().append(rxObj).append(".").append(name).append("='").append(Escapes.escape39(value)).append("';").newline();
        }
      } else {
        env.writer.tab().append(rxObj).append(".").append(name).append("=false;").newline();
      }
    }
    this.delayed = _delayed;
  }

  public void finish() {
    if (!delayed) {
      env.writer.tab().append(rxObj).append(".__();").newline();
    }
  }
}
