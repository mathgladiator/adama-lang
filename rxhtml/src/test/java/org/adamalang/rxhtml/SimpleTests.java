/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Root;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.File;

public class SimpleTests {

  private static String drive(String html) {
    Document document = Jsoup.parse(html);
    Environment env = Environment.fresh();
    for (Element element : document.getElementsByTag("template")) {
      Root.write(env.element(element));
    }
    return Root.finish(env);
  }

  @Test
  public void emptyvalue() {
    String result = drive("<template name=\"foo\">how<input checked /></template>");
    System.err.println(result);
  }

  @Test
  public void basic() {
    String result = drive("<template name=\"foo\">how<b class=\"foo bar\">d</b>y<img src=\"imgurl\"/></template>");
    System.err.println(result);
  }

  @Test
  public void single_var() {
    String result = drive("<template name=\"foo\"><lookup name=\"x\"/></template>");
    System.err.println(result);
  }

  @Test
  public void repeat_var() {
    String result = drive("<template name=\"foo\"><lookup name=\"x\"/><lookup name=\"x\"/><lookup name=\"x\"/></template>");
    System.err.println(result);
  }
}
