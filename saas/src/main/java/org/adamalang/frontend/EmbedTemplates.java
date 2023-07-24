/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.frontend;

import org.adamalang.common.Escaping;
import java.io.File;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class EmbedTemplates {
  public static void main(String[] args) throws Exception {
    File javaSpaceTemplates = new File("./saas/src/main/java/org/adamalang/frontend/SpaceTemplates.java");
    String java = Files.readString(javaSpaceTemplates.toPath());

    String start = "// BEGIN-TEMPLATES-POPULATE";
    String end = "// END-TEMPLATES-POPULATE";

    String prefix = java.substring(0, java.indexOf(start) + start.length());
    String suffix = java.substring(java.indexOf(end));
    StringBuilder sb = new StringBuilder();

    for (File file : new File("./saas/templates").listFiles((dir, name) -> name.endsWith(".adama"))) {
      String name = file.getName().replaceAll(Pattern.quote(".adama"), "");
      String adama = Files.readString(file.toPath());
      String rxhtml = "<forest></forest>";
      File rxhtmlFile = new File("./saas/templates/" + name + ".rxhtml");
      if (rxhtmlFile.exists()) {
        rxhtml = Files.readString(rxhtmlFile.toPath());
      }
      sb.append("    templates.put(\"").append(name).append("\", new SpaceTemplate(\"").append(new Escaping(adama).go()).append("\",\"").append(new Escaping(rxhtml).go()).append("\"));\n");
    }
    Files.writeString(javaSpaceTemplates.toPath(), prefix + "\n" + sb + "    " + suffix);
  }
}
