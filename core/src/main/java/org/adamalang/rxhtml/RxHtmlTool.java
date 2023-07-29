/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

import org.adamalang.common.web.UriMatcher;
import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Root;
import org.adamalang.rxhtml.template.Shell;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** the rxhtml tool for converting rxhtml into javascript templates */
public class RxHtmlTool {
  private static ArrayList<String> getDefaultRedirect(Document document) {
    ArrayList<String> defaults = new ArrayList<>();
    for (Element element : document.getElementsByTag("page")) {
      if (element.hasAttr("default-redirect-source")) {
        defaults.add(Root.uri_to_instructions(element.attr("uri")).formula);
      }
    }
    return defaults;
  }

  private static String buildInternStyle(Document document) {
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

  private static String buildCustomJavaScript(Document document) {
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

  public static RxHtmlResult convertStringToTemplateForest(String str, ShellConfig config) {
    Environment env = Environment.fresh(config.feedback);
    Document document = Jsoup.parse(str);
    Root.start(env, buildCustomJavaScript(document));
    String style = buildInternStyle(document);
    ArrayList<String> defaultRedirects = getDefaultRedirect(document);
    Shell shell = new Shell(config);
    shell.scan(document);
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
    return new RxHtmlResult(javascript, style, shell, patterns, env.getCssFreq());
  }

  public static RxHtmlResult convertFilesToTemplateForest(List<File> files, ArrayList<UriMatcher> matchers, ShellConfig config) throws Exception {
    Environment env = Environment.fresh(config.feedback);
    String bundled = Bundler.bundle(files);
    Shell shell = new Shell(config);
    ArrayList<String> patterns = new ArrayList<>();
    Document document = Jsoup.parse(bundled, "UTF-8");
    Root.start(env, buildCustomJavaScript(document));
    String style = buildInternStyle(document);
    shell.scan(document);
    ArrayList<String> defaultRedirects = getDefaultRedirect(document);
    for (Element element : document.getElementsByTag("template")) {
      Root.template(env.element(element, true));
    }
    for (Element element : document.getElementsByTag("page")) {
      matchers.add(RxHtmlToAdama.uriOf(element.attr("uri")).matcher());
      patterns.add(element.attr("uri"));
      Root.page(env.element(element, true), defaultRedirects);
    }
    String javascript = Root.finish(env);
    return new RxHtmlResult(javascript, style, shell, patterns, env.getCssFreq());
  }
}
