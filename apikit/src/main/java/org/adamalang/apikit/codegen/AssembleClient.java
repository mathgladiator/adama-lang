/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Method;

public class AssembleClient {
  public static String make(Method[] methods) throws Exception {
    StringBuilder ts = new StringBuilder();
    ts.append("export class AdamaAPI {\n");
    for (Method method : methods) {
      ts.append("  // ").append(method.camelName).append(" --> ").append(method.name).append("\n\n");
    }
    ts.append("}");
    return ts.toString();
  }
}
