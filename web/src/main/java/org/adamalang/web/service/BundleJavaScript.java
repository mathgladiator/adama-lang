/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.service;

import org.adamalang.common.DefaultCopyright;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class BundleJavaScript {
  public static void main(String[] args) throws Exception {
    {
      StringBuilder sb = new StringBuilder();
      sb.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
      sb.append("package org.adamalang.web.service;\n\n");
      sb.append("import java.util.Base64;\n\n");
      sb.append("public class JavaScriptClient {\n");
      sb.append("  public static final byte[] ADAMA_JS_CLIENT_BYTES = Base64.getDecoder().decode(\"");
      String str = Files.readString(new File("./release/libadama.js").toPath());
      str = new String(Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8)));
      sb.append(str);
      sb.append("\");\n");
      sb.append("}");
      Files.writeString(new File("web/src/main/java/org/adamalang/web/service/JavaScriptClient.java").toPath(), sb.toString());
    }
    {
      StringBuilder sb = new StringBuilder();
      sb.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
      sb.append("package org.adamalang.web.service;\n\n");
      sb.append("import java.util.Base64;\n\n");
      sb.append("public class JavaScriptRxHtml {\n");
      sb.append("  public static final byte[] RXHTML_JS_BYTES = Base64.getDecoder().decode(\"");
      String str = Files.readString(new File("./release/rxhtml.js").toPath());
      str = new String(Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8)));
      sb.append(str);
      sb.append("\");\n");
      sb.append("}");
      Files.writeString(new File("web/src/main/java/org/adamalang/web/service/JavaScriptRxHtml.java").toPath(), sb.toString());
    }
  }
}
