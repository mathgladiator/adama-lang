/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

public class CodeGenAuth {
  public static void writeAuth(final StringBuilderWithTabs sb, Environment environment) {
    sb.append("@Override").writeNewline();
    sb.append("public String __auth(String username, String password) {").tabUp().writeNewline();
    sb.append("return null;").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}
