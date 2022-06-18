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

public class Scope {
  public static void write(Environment env) {
    String path = env.element.attr("path");
    String newStateVar = env.pool.ask();
    // TODO: manipulate the state and evalute the path
    env.writer.tab().append("var ").append(newStateVar).append(" = $.S(").append(env.stateVar).append(", '").append(path).append("');").newline();
    Base.children(env.stateVar(newStateVar));
  }
}
