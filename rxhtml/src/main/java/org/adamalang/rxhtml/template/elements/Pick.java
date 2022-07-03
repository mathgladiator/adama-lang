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
import org.adamalang.rxhtml.template.RxAttributeObject;

public class Pick {
  public static void write(Environment env) {
    RxAttributeObject obj = new RxAttributeObject(env, "name");
    String sVar = env.pool.ask();
    final String parentVar;
    if (env.singleParent) {
      parentVar = env.parentVariable;
    } else {
      parentVar = env.pool.ask();
      env.writer.tab().append("var ").append(parentVar).append("=$.e('div');").newline();
      env.writer.tab().append(env.parentVariable).append(".append(").append(parentVar).append(");").newline();
    }
    env.writer.tab().append("$.P(").append(parentVar).append(",").append(env.stateVar).append(",").append(obj.rxObj).append(",function(").append(sVar).append(") {").tabUp().newline();
    Base.children(env.stateVar(sVar).parentVariable(parentVar));
    env.writer.tabDown().tab().append("});").newline();
    obj.finish();
    env.pool.give(sVar);
  }
}
