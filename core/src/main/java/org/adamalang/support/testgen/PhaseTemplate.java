/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.support.testgen;

public class PhaseTemplate {
  public static String inventEmpty() {
    final var templateBuilder = new StringBuilder();
    templateBuilder.append("\n");
    templateBuilder.append("@construct {\n");
    templateBuilder.append("}\n");
    templateBuilder.append("\n");
    templateBuilder.append("test PrimaryTest {\n");
    templateBuilder.append("  assert false;\n");
    templateBuilder.append("}\n");
    return templateBuilder.toString();
  }
}
