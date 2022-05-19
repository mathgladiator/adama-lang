package org.adamalang.rxhtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;

public class RxHtmlTool {
  public static String convertFilesToTemplateForest(ArrayList<File> files) throws Exception {
    Template template = new Template();
    for (File file : files) {
      Document document = Jsoup.parse(file, "UTF-8");
      for (Element element : document.getElementsByTag("template")) {
        template.writeRoot(element);
      }
    }
    return template.finish();
  }
}
