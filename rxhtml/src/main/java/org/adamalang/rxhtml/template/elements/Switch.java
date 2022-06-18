/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml.template.elements;

import org.adamalang.rxhtml.template.Base;
import org.adamalang.rxhtml.template.Environment;
import org.jsoup.nodes.Element;

public class Switch {
  public static void write(Environment env) {
    env.assertHasParent();
    env.parent.assertSoloParent();

    String path = env.element.attr("path");
    String stateVarToUse = env.stateVar;

    // TODO: parse the path to get a state variable
    String childStateVar = env.pool.ask();
    String caseVar = env.pool.ask();
    env.writer.tab().append("$.W(").append(env.parentVariable).append(", ").append(stateVarToUse);
    env.writer.append(", '").append(path).append("', function(").append(childStateVar).append(", ").append(caseVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(childStateVar).caseVar(caseVar));
    env.writer.tabDown().tab().append("});").newline();
    env.pool.give(caseVar);
    env.pool.give(childStateVar);
  }
}
