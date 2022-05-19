package org.adamalang.rxhtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

public class TemplateTests {
  @Test
  public void basic() {
    Document document = Jsoup.parse("<template name=\"foo\">how<b class=\"foo bar\">d</b>y<img src=\"imgurl\"/></template>");
    Template template = new Template();
    for (Element element : document.getElementsByTag("template")) {
      template.writeRoot(element);
    }
    System.err.println(template.finish());
  }
}
