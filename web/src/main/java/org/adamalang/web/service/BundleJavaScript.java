/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import org.adamalang.common.DefaultCopyright;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class BundleJavaScript {
  public static String bundle(String file) throws Exception {
    String str = Files.readString(new File(file).toPath());
    StringBuilder sb = new StringBuilder();
    sb.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
    sb.append("package org.adamalang.web.service;\n\n");
    sb.append("import java.util.Base64;\n\n");
    sb.append("public class JavaScriptClient {\n");
    sb.append("  public static final byte[] ADAMA_JS_CLIENT_BYTES = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8))));
    sb.append("}");
    return sb.toString();
  }

  public static void appendStringInChunks(StringBuilder sb, String str) {
    sb.append("Base64.getDecoder().decode(make());\n");
    sb.append("  private static String make() {\n");
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
