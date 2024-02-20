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
package org.adamalang.rxhtml;

import org.adamalang.common.StringHelper;
import org.adamalang.common.html.InjectCoordInline;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** simple bundler to repackage a set of RxHTML files into one file */
public class Bundler {
  public static String bundle(File commonPath, List<File> files, boolean inject) throws Exception {
    StringBuilder output = new StringBuilder();
    output.append("<forest>\n");
    for (File file : files) {
      String nameToReport = file.getName();
      try {
        String commonRoot = commonPath.getAbsolutePath();
        String fullPath = file.getAbsolutePath();
        nameToReport = fullPath.substring(commonRoot.length()).replaceAll(Pattern.quote("\\"), Matcher.quoteReplacement("/"));
      } catch (Exception failedToResolveAbsolutePath) {
      }
      Document useDoc;
      if (inject) {
        useDoc = Jsoup.parse(InjectCoordInline.execute(Files.readString(file.toPath()), nameToReport));
      } else {
        useDoc = Jsoup.parse(file);
      }
      output.append(StringHelper.splitNewlineAndTabify(useDoc.getElementsByTag("forest").html().replaceAll("\r", ""), ""));
    }
    output.append("</forest>\n");
    return output.toString();
  }
}
