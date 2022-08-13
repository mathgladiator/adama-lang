/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Root;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.List;

/** the rxhtml tool for converting rxhtml into javascript templates */
public class RxHtmlTool {
  private static String getDefaultRedirect(Document document) {
    String defaultRedirect = null;
    for (Element element : document.getElementsByTag("page")) {
      if (element.hasAttr("default-redirect-source")) {
        defaultRedirect = element.attr("uri");
      }
    }
    return defaultRedirect;
  }

  public static String convertStringToTemplateForest(String str, Feedback feedback) {
    Environment env = Environment.fresh(feedback);
    Root.start(env);
    Document document = Jsoup.parse(str);
    String defaultRedirect = getDefaultRedirect(document);
    for (Element element : document.getElementsByTag("template")) {
      Root.template(env.element(element, true));
    }
    for (Element element : document.getElementsByTag("page")) {
      Root.page(env.element(element, true), defaultRedirect);
    }
    // TODO: do warnings about cross-page linking, etc...
    return Root.finish(env);
  }

  public static String convertFilesToTemplateForest(List<File> files, Feedback feedback) throws Exception {
    Environment env = Environment.fresh(feedback);
    Root.start(env);
    for (File file : files) {
      Document document = Jsoup.parse(file, "UTF-8");
      String defaultRedirect = getDefaultRedirect(document);
      for (Element element : document.getElementsByTag("template")) {
        Root.template(env.element(element, true));
      }
      for (Element element : document.getElementsByTag("page")) {
        Root.page(env.element(element, true), defaultRedirect);
      }
    }
    return Root.finish(env);
  }
}
