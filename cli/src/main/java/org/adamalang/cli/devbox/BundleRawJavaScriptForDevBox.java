/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import org.adamalang.common.DefaultCopyright;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

/** tool for embedding javascript for devbox when not overridden */
public class BundleRawJavaScriptForDevBox {
  public static String bundle(File root) throws Exception {
    String tree = Files.readString(new File(root, "tree.js").toPath());
    String debugger = Files.readString(new File(root, "debugger.js").toPath());
    String rxhtml = Files.readString(new File(root, "rxhtml.js").toPath());
    String connection = Files.readString(new File(root, "connection.js").toPath());
    StringBuilder sb = new StringBuilder();
    sb.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
    sb.append("package org.adamalang.cli.devbox;\n\n");
    sb.append("import java.nio.charset.StandardCharsets;\n");
    sb.append("import java.util.Base64;\n\n");
    sb.append("public class JavaScriptResourcesRaw {\n");
    sb.append("  public static final String TREE = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(tree.getBytes(StandardCharsets.UTF_8))), "tree");
    sb.append("  public static final String DEBUGGER = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(debugger.getBytes(StandardCharsets.UTF_8))), "debugger");
    sb.append("  public static final String RXHTML = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(rxhtml.getBytes(StandardCharsets.UTF_8))), "rxhtml");
    sb.append("  public static final String CONNECTION = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(connection.getBytes(StandardCharsets.UTF_8))), "connection");
    sb.append("}");
    return sb.toString();
  }

  public static void appendStringInChunks(StringBuilder sb, String str, String name) {
    sb.append("new String(Base64.getDecoder().decode(make_" + name + "()), StandardCharsets.UTF_8);\n");
    sb.append("  private static String make_" + name + "() {\n");
    sb.append("    StringBuilder sb = new StringBuilder();\n");
    int len = str.length();
    int at = 0;
    while (at < len) {
      int sz = Math.min(80, len - at);
      String fragment = str.substring(at, at + sz);
      sb.append("    sb.append(\"").append(fragment).append("\");\n");
      at += sz;
    }
    sb.append("    return sb.toString();\n");
    sb.append("  }\n");
  }
}
