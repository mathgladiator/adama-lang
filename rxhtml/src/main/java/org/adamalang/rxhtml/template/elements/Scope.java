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
    String into = env.element.attr("into");
    String oldDelta = env.pool.ask();
    env.writer.tab().append("{").tabUp().newline();
    env.writer.tab().append("var ").append(oldDelta).append(" = _;").newline();
    env.writer.tab().append("_ = {};").newline();
    Base.children(env.current(env.current + "." + into).resetSubscriptionCounts());
    env.writer.tab().append(oldDelta).append(".").append(into).append(" = _;").newline();
    env.writer.tab().append("_ = ").append(oldDelta).append(";").newline();
    env.writer.tabDown().tab().append("}").newline();
    env.pool.give(oldDelta);
  }
}
