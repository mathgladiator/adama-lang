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

import org.adamalang.rxhtml.template.Environment;

public class Connection {
  public static void write(Environment env) {
    String name = env.element.attr("name");
    String space = env.element.attr("space");
    // TODO: VALIDATE and check that name and space are constants

    String identity = env.element.attr("identity");
    // TODO: validate identity is a JWT token if present

    String key = env.element.attr("key");
    // TODO: figure out key reactivity
    boolean randomPrefix = env.element.hasAttr("random-key-suffix");

    env.writer.tab().append("$.connect('").append(name).append("'");
    if (identity != null) {
      env.writer.append(", '").append(identity).append("'");
    } else {
      env.writer.append(", null");
    }
    env.writer.append(", '").append(space).append("'");
    env.writer.append(", '").append(key).append("'");
    // TODO: the reactivity of the key is very interesting
    env.writer.append(randomPrefix ? ", true" : ", false");
    env.writer.append(", function () {").tabUp().newline();
    Pick.write(env);
    env.writer.tabDown().tab().append("});").newline();
  }
}
