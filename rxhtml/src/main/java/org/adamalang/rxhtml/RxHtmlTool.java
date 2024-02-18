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

import org.adamalang.rxhtml.preprocess.MarkStaticContent;
import org.adamalang.rxhtml.preprocess.Pagify;
import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Root;
import org.adamalang.rxhtml.template.Shell;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.rxhtml.typing.ViewSchemaBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;

/** the rxhtml tool for converting rxhtml into javascript templates */
public class RxHtmlTool {
  public static RxHtmlBundle convertStringToTemplateForest(String str, File types, ShellConfig config) {
    Environment env = Environment.fresh(config.feedback, config.environment);
    // DISABLED UNTIL RELEASE
    // TypeChecker.typecheck(str, types, config.feedback);
    Document document = Jsoup.parse(str);
    // TODO: enabling this requires a LOT of work
    // MarkStaticContent.mark(document);
    Pagify.pagify(document);
    Root.start(env, buildCustomJavaScript(document));
    String style = buildInternStyle(document);
    ArrayList<String> defaultRedirects = getDefaultRedirect(document);
    Shell shell = new Shell(config);
    shell.scan(document);
    ViewSchemaBuilder vb = new ViewSchemaBuilder(document, config.feedback);
    ArrayList<String> patterns = new ArrayList<>();
    for (Element element : document.getElementsByTag("template")) {
      Root.template(env.element(element, true));
    }
    for (Element element : document.getElementsByTag("page")) {
      patterns.add(element.attr("uri"));
      Root.page(env.element(element, true), defaultRedirects);
    }
    // TODO: do warnings about cross-page linking, etc...
    String javascript = Root.finish(env);
    return new RxHtmlBundle(javascript, style, shell, patterns, env.getCssFreq(), env.tasks, vb.results);
  }

  public static String buildCustomJavaScript(Document document) {
    StringBuilder customjs = new StringBuilder();
    ArrayList<Element> axe = new ArrayList<>();
    for (Element element : document.getElementsByTag("script")) {
      if (element.hasAttr("is-custom")) {
        customjs.append(element.html().trim());
        axe.add(element);
      }
    }
    for (Element toAxe : axe) {
      toAxe.remove();
    }
    return customjs.toString();
  }

  public static String buildInternStyle(Document document) {
    ArrayList<Element> axe = new ArrayList<>();
    StringBuilder style = new StringBuilder();
    for (Element element : document.getElementsByTag("style")) {
      style.append(element.html().trim()).append(" ");
      axe.add(element);
    }
    for (Element toAxe : axe) {
      toAxe.remove();
    }
    return style.toString().trim();
  }

  public static ArrayList<String> getDefaultRedirect(Document document) {
    ArrayList<String> defaults = new ArrayList<>();
    for (Element element : document.getElementsByTag("page")) {
      if (element.hasAttr("default-redirect-source")) {
        defaults.add(Root.uri_to_instructions(element.attr("uri")).formula);
      }
    }
    return defaults;
  }
}
