/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

import org.adamalang.common.web.UriMatcher;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Escapes;
import org.adamalang.rxhtml.template.Root;
import org.adamalang.rxhtml.template.Shell;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  // SKETCH of things to come
  /*
  private static String buildDefaultRedirectSource(Document document) {
    StringBuilder redirect = new StringBuilder();
    String defaultRedirect = null;
    for (Element element : document.getElementsByTag("page")) {
      if (element.hasAttr("default-redirect-source")) {
        String value = element.attr("uri");


        Tree tree = Parser.parse(value);
        Map<String, String> vars = tree.variables();
        if (vars.size() == 0) {
          defaultRedirect = Escapes.constantOf(value);
        } else {
          // defaultRedirect = "function(){ return \"" + tree.js(oVar) + "\";}";
        }
      }
    }
    return defaultRedirect;
  }
  */

  public static RxHtmlResult convertStringToTemplateForest(String str, Feedback feedback) {
    Environment env = Environment.fresh(feedback);
    Root.start(env);
    Document document = Jsoup.parse(str);
    String defaultRedirect = getDefaultRedirect(document);
    StringBuilder style = new StringBuilder();
    Shell shell = new Shell(feedback);
    shell.scan(document);
    ArrayList<String> patterns = new ArrayList<>();
    for (Element element : document.getElementsByTag("template")) {
      Root.template(env.element(element, true));
    }
    for (Element element : document.getElementsByTag("style")) {
      style.append(element.html()).append(" ");
    }
    for (Element element : document.getElementsByTag("page")) {
      patterns.add(element.attr("uri"));
      Root.page(env.element(element, true), defaultRedirect);
    }
    // TODO: do warnings about cross-page linking, etc...
    String javascript = Root.finish(env);
    return new RxHtmlResult(javascript, style.toString(), shell, patterns);
  }

  public static RxHtmlResult convertFilesToTemplateForest(List<File> files, ArrayList<UriMatcher> matchers, Feedback feedback) throws Exception {
    Environment env = Environment.fresh(feedback);
    Root.start(env);
    Shell shell = new Shell(feedback);
    StringBuilder style = new StringBuilder();
    ArrayList<String> patterns = new ArrayList<>();
    for (File file : files) {
      Document document = Jsoup.parse(file, "UTF-8");
      shell.scan(document);
      String defaultRedirect = getDefaultRedirect(document);
      for (Element element : document.getElementsByTag("template")) {
        Root.template(env.element(element, true));
      }
      for (Element element : document.getElementsByTag("style")) {
        style.append(element.html()).append(" ");
      }
      for (Element element : document.getElementsByTag("page")) {
        matchers.add(RxHtmlToAdama.uriOf(element.attr("uri")).matcher());
        patterns.add(element.attr("uri"));
        Root.page(env.element(element, true), defaultRedirect);
      }
    }
    String javascript = Root.finish(env);
    return new RxHtmlResult(javascript, style.toString(), shell, patterns);
  }
}
