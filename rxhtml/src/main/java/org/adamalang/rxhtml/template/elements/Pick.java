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

public class Pick {
  public static void write(Environment env) {
    String sVar = env.pool.ask();
    String name = env.element.attr("name");
    env.writer.tab().append("{").tabUp().newline();
    env.writer.tab().append("var ").append(sVar).append(" = $.P('").append(name).append("',").append(env.stateVar).append(");").newline();
    // TODO: figure out how to handle a reactive name
    Base.children(env.stateVar(sVar));
    env.writer.tab().append(sVar).append(".bind();").newline();
    env.pool.give(sVar);
    env.writer.tabDown().tab().append("}").newline();
  }
}
