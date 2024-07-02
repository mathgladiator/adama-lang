/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.devbox;

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
    String tester = Files.readString(new File(root, "tester.js").toPath());
    String connection = Files.readString(new File(root, "connection.js").toPath());
    String worker = Files.readString(new File(root, "worker.js").toPath());
    StringBuilder sb = new StringBuilder();
    sb.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
    sb.append("package org.adamalang.devbox;\n\n");
    sb.append("import java.nio.charset.StandardCharsets;\n");
    sb.append("import java.util.Base64;\n\n");
    sb.append("public class JavaScriptResourcesRaw {\n");
    sb.append("  public static final String TREE = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(tree.getBytes(StandardCharsets.UTF_8))), "tree");
    sb.append("  public static final String DEBUGGER = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(debugger.getBytes(StandardCharsets.UTF_8))), "debugger");
    sb.append("  public static final String RXHTML = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(rxhtml.getBytes(StandardCharsets.UTF_8))), "rxhtml");
    sb.append("  public static final String TESTER = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(tester.getBytes(StandardCharsets.UTF_8))), "tester");
    sb.append("  public static final String CONNECTION = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(connection.getBytes(StandardCharsets.UTF_8))), "connection");
    sb.append("  public static final String WORKER = ");
    appendStringInChunks(sb, new String(Base64.getEncoder().encode(worker.getBytes(StandardCharsets.UTF_8))), "worker");
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
