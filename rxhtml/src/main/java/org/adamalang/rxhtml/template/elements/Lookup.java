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

public class Lookup {
  public static void write(Environment env) {
    env.assertHasParent();
    String path = env.element.attr("path");
    String transform = env.element.attr("transform");
    String stateVarToUse = env.stateVar;
    // TODO: parse the path to get a state variable
    if (transform == null || "".equals(transform)) {
      env.writer.tab().append(env.parentVariable).append(".append($.L(").append(stateVarToUse).append(", '").append(path).append("'));").newline();
    } else {
      // TODO sort out transforms
    }
  }
}
