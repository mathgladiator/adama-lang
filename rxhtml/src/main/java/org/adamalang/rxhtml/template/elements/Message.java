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
import org.adamalang.rxhtml.template.Fix;

public class Message {
  public static void write(Environment env) {
    if (env.formVariable != null) {
      // ASSERT
    }
    String local = Fix.ident(env.element.attr("local"));
    String channel = Fix.ident(env.element.attr("channel"));
    if (local == null && channel == null) {
      // ASSERT
    }
    if (local != null && channel != null) {
      // ASSERT
    }
    if (local != null) {
      env.element.removeAttr("local");
    }
    if (channel != null) {
      env.element.removeAttr("channel");
    }
    env.element.tagName("form");
    env.writer.tab().append("{").tabUp().newline();
    env.writer.tab().append("_$ = [];").newline();
    String formVar = Base.write(env, true);
    if (local != null) {
      env.writer.tab().append("$.z('").append(local).append("',").append(formVar).append(",_$);").newline();
    }
    if (channel != null) {

    }
    env.writer.tabDown().tab().append("}").newline();
    env.pool.give(formVar);
  }
}
