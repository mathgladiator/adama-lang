/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.edhtml.phases;

import org.adamalang.common.StringHelper;
import org.adamalang.edhtml.EdHtmlState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

/** simply bring in the given file into the output; this has the primary response of allowing projects to be split across files */
public class Use {
  public static void execute(EdHtmlState state) throws Exception {
    Elements uses = state.document.getElementsByTag("use");
    for (Element use : uses) {
      String fileToLink = use.attr("file");
      Document useDoc = Jsoup.parse(new File(state.includes_path, fileToLink));
      state.output_rx.append(StringHelper.splitNewlineAndTabify(useDoc.getElementsByTag("forest").html(), ""));
    }
  }
}
