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
package org.adamalang.frontend;

import org.adamalang.common.Escaping;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

public class EmbedTemplates {
  public static void doit() throws Exception {
    File javaSpaceTemplates = new File("./saas/src/main/java/org/adamalang/frontend/SpaceTemplates.java");
    String java = Files.readString(javaSpaceTemplates.toPath());

    String start = "// BEGIN-TEMPLATES-POPULATE";
    String end = "// END-TEMPLATES-POPULATE";

    String prefix = java.substring(0, java.indexOf(start) + start.length());
    String suffix = java.substring(java.indexOf(end));
    StringBuilder sb = new StringBuilder();
    ArrayList<File> files = new ArrayList<>();
    for(File file : new File("./saas/templates").listFiles((dir, name) -> name.endsWith(".adama"))) {
      files.add(file);
    }
    files.sort(Comparator.comparing(File::getName));
    for (File file : files) {
      String name = file.getName().replaceAll(Pattern.quote(".adama"), "");
      String adama = Files.readString(file.toPath());
      String rxhtml = "<forest></forest>";
      File rxhtmlFile = new File("./saas/templates/" + name + ".rx.html");
      if (rxhtmlFile.exists()) {
        rxhtml = Files.readString(rxhtmlFile.toPath());
      }
      sb.append("    templates.put(\"").append(name).append("\", new SpaceTemplate(\"").append(new Escaping(adama).go()).append("\",\"").append(new Escaping(rxhtml).go()).append("\"));\n");
    }
    Files.writeString(javaSpaceTemplates.toPath(), prefix + "\n" + sb + "    " + suffix);
  }
}
