/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.web.service;

import org.adamalang.common.DefaultCopyright;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BundleJavaScript {
  public static String bundle(String fileJs, String fileWorker) throws Exception {
    String strJs = Files.readString(new File(fileJs).toPath());
    String strWorker = Files.readString(new File(fileWorker).toPath());
    StringBuilder sb = new StringBuilder();
    sb.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
    sb.append("package org.adamalang.web.service;\n\n");
    sb.append("import java.nio.charset.StandardCharsets;\n");
    sb.append("import java.util.Base64;\n");
    sb.append("import java.util.regex.Matcher;\n");
    sb.append("import java.util.regex.Pattern;\n");
    sb.append("\n");
    sb.append("public class JavaScriptClient {\n");
    sb.append("  public static final byte[] ADAMA_JS_CLIENT_BYTES = ");
    appendStringInChunks(sb, "c", new String(Base64.getEncoder().encode(strJs.getBytes(StandardCharsets.UTF_8))));
    sb.append("  public static final byte[] BETA_ADAMA_JS_CLIENT_BYTES = new String(ADAMA_JS_CLIENT_BYTES, StandardCharsets.UTF_8).replaceAll(Pattern.quote(\"Adama.Production\"), Matcher.quoteReplacement(\"Adama.Beta\")).getBytes(StandardCharsets.UTF_8);\n");
    sb.append("  public static final byte[] ADAMA_WORKER_JS_CLIENT_BYTES = ");
    appendStringInChunks(sb, "w", new String(Base64.getEncoder().encode(strWorker.getBytes(StandardCharsets.UTF_8))));
    sb.append("  public static final byte[] BETA_ADAMA_WORKER_JS_CLIENT_BYTES = ADAMA_WORKER_JS_CLIENT_BYTES;\n");
    sb.append("}");
    return sb.toString();
  }

  public static void appendStringInChunks(StringBuilder sb, String suffix, String str) {
    sb.append("Base64.getDecoder().decode(make").append(suffix).append("());\n");
    sb.append("  private static String make").append(suffix).append("() {\n");
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
