/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.template.Parser;
import org.adamalang.common.template.Settings;
import org.adamalang.common.template.tree.T;
import org.adamalang.rxhtml.routing.Target;
import org.adamalang.rxhtml.routing.Instructions;
import org.adamalang.rxhtml.routing.Table;
import org.adamalang.rxhtml.template.Environment;
import org.adamalang.rxhtml.template.Root;
import org.adamalang.rxhtml.template.Shell;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.rxhtml.typing.ViewSchemaBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/** the rxhtml tool for converting rxhtml into javascript templates */
public class RxHtmlTool {
  public static RxHtmlBundle convertStringToTemplateForest(String str, File types, ShellConfig config) {
    Environment env = Environment.fresh(config.feedback, config.environment);
    TypeChecker.typecheck(str, types, config.feedback);
    Document document = Loader.parseForest(str, config.feedback, ProductionMode.Web);
    Root.start(env, buildCustomJavaScript(document));
    String style = buildInternStyle(document);
    ArrayList<String> defaultRedirects = getDefaultRedirect(document);
    Shell shell = new Shell(config);
    shell.scan(document);
    ViewSchemaBuilder vb = new ViewSchemaBuilder(document, config.feedback);
    for (Element element : document.getElementsByTag("template")) {
      Root.template(env.element(element, true, null));
    }
    Table table = new Table();
    ArrayList<String> urisToMapToEntire = new ArrayList<>();
    for (Element element : document.getElementsByTag("page")) {
      urisToMapToEntire.add(element.attr("uri"));
      Root.page(env.element(element, true, null), defaultRedirects);
    }
    // TODO: do warnings about cross-page linking, etc...
    String javascript = Root.finish(env);

    // build the table
    TreeMap<String, String> entireHeaders = new TreeMap<>();
    entireHeaders.put("content-type", "text/html; charset=UTF-8");
    entireHeaders.put("cache-control", "public, max-age=" + config.cacheMaxAgeSeconds + ", min-fresh=" + config.cacheMaxAgeSeconds);
    Target entire = new Target(200, entireHeaders, shell.makeShell(javascript, style).getBytes(StandardCharsets.UTF_8), null);
    for (String uri : urisToMapToEntire) {
      table.add(Instructions.parse(uri), entire);
    }
    for (Element element : document.getElementsByTag("static-rewrite")) {
      String uri = element.attr("uri");
      String location = element.attr("location");
      int status = "302".equals(element.attr("status")) ? 302 : 301;
      if (location != null) {
        table.add(Instructions.parse(uri), createRedirectRule(status, location));
      }
    }
    Diagnostics diagnostics = new Diagnostics(env.getCssFreq(), env.tasks, vb.results, javascript.length());
    return new RxHtmlBundle(javascript, style, shell, diagnostics, table);
  }

  public static Target createRedirectRule(int status, String location) {
    final T locationTemplate = Parser.parse(location);
    return new Target(999, null, null, (t, cap) -> {
      TreeMap<String, String> headers = new TreeMap<>();
      StringBuilder result = new StringBuilder();
      ObjectNode params = Json.newJsonObject();
      for (Map.Entry<String, String> c : cap.entrySet()) {
        params.put(c.getKey(), c.getValue());
      }
      locationTemplate.render(new Settings(false), params, result);
      headers.put("location", result.toString());
      return new Target(status, headers, null, null);
    });
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
        defaults.add(Instructions.parse(element.attr("uri")).formula);
      }
    }
    return defaults;
  }
}
