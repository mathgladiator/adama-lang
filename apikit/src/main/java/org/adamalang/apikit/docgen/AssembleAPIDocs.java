/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.apikit.docgen;

import org.adamalang.apikit.model.Method;

import java.util.regex.Pattern;

public class AssembleAPIDocs {

  public static String docify(Method[] methods) {
    StringBuilder markdown = new StringBuilder();
    markdown.append("# API Reference \n");
    markdown.append("\n");

    for (Method method : methods) {
      markdown.append("## Method: ").append(method.name).append("\n");
      for (String ln : method.documentation.trim().split(Pattern.quote("\n"))) {
        markdown.append(ln.trim()).append("\n");
      }
      markdown.append("\n");
    }

    return markdown.toString();
  }
}
