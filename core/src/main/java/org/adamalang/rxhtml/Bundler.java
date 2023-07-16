/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

import org.adamalang.common.StringHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.List;

/** simple bundler to repackage a set of RxHTML files into one file */
public class Bundler {
  public static String bundle(List<File> files) throws Exception {
    StringBuilder output = new StringBuilder();
    output.append("<forest>\n");
    for (File file : files) {
      Document useDoc = Jsoup.parse(file);
      output.append(StringHelper.splitNewlineAndTabify(useDoc.getElementsByTag("forest").html(), ""));
    }
    output.append("</forest>\n");
    return output.toString();
  }
}
