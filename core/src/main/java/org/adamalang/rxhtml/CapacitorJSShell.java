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

import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Root;
import org.adamalang.rxhtml.template.config.Feedback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CapacitorJSShell {

  public static String makeMobileShell(String forest, String domainOverride, Feedback feedback) throws Exception {
    StringBuilder sb = new StringBuilder();
    StringBuilder scripts = new StringBuilder();
    Document document = Jsoup.parse(forest);
    Element mobileShell = findMobileShell(document);
    boolean worker = "true".equalsIgnoreCase(mobileShell.attr("worker"));
    String workerIdentity = mobileShell.hasAttr("worker-identity-name") ? mobileShell.attr("worker-identity-name") : "default";
    sb.append("<!DOCTYPE html>\n<html");
    if (mobileShell.hasAttr("html-class")) {
      sb.append(" class=\"").append(mobileShell.attr("html-class")).append("\"");
    }
    sb.append(">\n");
    String defaultTitle = null;
    for (Element element : mobileShell.getElementsByTag("title")) {
      defaultTitle = element.text();
    }
    if (defaultTitle != null) {
      sb.append("  <title>").append(defaultTitle).append("</title>\n");
    }
    for (Element element : mobileShell.getElementsByTag("meta")) {
      sb.append(element.toString()).append("\n");
    }
    for (Element element : mobileShell.getElementsByTag("link")) {
      sb.append(element.toString()).append("\n");
    }
    for (Element element : mobileShell.getElementsByTag("script")) {
      scripts.append(element.toString()).append("\n");
    }
    sb.append("<script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script>").append("\n");
    Environment env = Environment.fresh(feedback, "mobile");
    Root.start(env, RxHtmlTool.buildCustomJavaScript(document));
    for (Element element : document.getElementsByTag("template")) {
      // TODO: detect if this template is used by any mobile pages
      Root.template(env.element(element, true));
    }
    ArrayList<String> defaultRedirects = RxHtmlTool.getDefaultRedirect(document);
    for (Element element : document.getElementsByTag("page")) {
      // TODO: discriminate for a mobile page (and also, get a dependency tree of templates)
      Root.page(env.element(element, true), defaultRedirects);
    }
    String javascript = Root.finish(env);
    sb.append("<script>\n\n").append(javascript).append("\n\n</script>\n");
    sb.append("<style>\n\n").append(RxHtmlTool.buildInternStyle(document)).append("\n\n</style>\n");
    sb.append("</body><script>\n");
    sb.append("  RxHTML.init();\n");
    if (domainOverride != null) {
      sb.append("  RxHTML.mobileInit(\"").append(domainOverride).append("\");\n");
    }
    if (worker) {
      sb.append("  RxHTML.worker(\""+workerIdentity+"\",\"/libadama-worker.js\");\n");
    }
    sb.append("</script></html>");
    return sb.toString();
  }

  public static Element findMobileShell(Document document) throws Exception {
    Elements elements = document.getElementsByTag("mobile-shell");
    if (elements.size() == 1) {
      return elements.get(0);
    }
    throw new Exception("failed to find a solo <mobile-shell> element");
  }
}
