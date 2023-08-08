/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;

import java.util.ArrayList;
import java.util.TreeSet;

public class CodeGenViewStateFilter {
  public static void writeViewStateFilter(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("@Override").writeNewline();
    sb.append("public String __getViewStateFilter() {").tabUp().writeNewline();
    ArrayList<String> quotedKeys = new ArrayList<>();
    for (String key : new TreeSet<>(environment.document.viewerType.storage.fields.keySet())) {
      quotedKeys.add("\\\"" + key + "\\\"");
    }
    sb.append("return \"[" + String.join(", ", quotedKeys) + "]\";").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}
