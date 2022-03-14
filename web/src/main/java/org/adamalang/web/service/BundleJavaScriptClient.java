package org.adamalang.web.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class BundleJavaScriptClient {
  public static void main(String[] args) throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("package org.adamalang.web.service;\n\n");
    sb.append("import java.nio.charset.StandardCharsets;\n\n");
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
}