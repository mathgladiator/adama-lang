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
package org.adamalang.rxhtml.template;

import org.adamalang.common.Platform;
import org.adamalang.rxhtml.RxHtmlBundle;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/** a class for pulling out the first and (hopefully) only shell */
public class Shell {
  private final ShellConfig config;
  private Element shell;

  public Shell(ShellConfig config) {
    this.config = config;
    this.shell = null;
  }

  public void scan(Document document) {
    for (Element element : document.getElementsByTag("shell")) {
      if (this.shell == null) {
        this.shell = element;
      } else {
        config.feedback.warn(element, "A duplicate shell was found");
      }
    }
  }

  public String makeShell(String javascript, String style) {
    StringBuilder sb = new StringBuilder();
    StringBuilder scripts = new StringBuilder();
    boolean worker = false;
    String workerIdentity = "default";
    if (shell != null) {
      worker = "true".equalsIgnoreCase(shell.attr("worker"));
      sb.append("<!DOCTYPE html>\n<html");
      if (shell.hasAttr("html-class")) {
        sb.append(" class=\"").append(shell.attr("html-class")).append("\"");
      }
      if (shell.hasAttr("worker-identity-name")) {
        workerIdentity = shell.attr("worker-identity-name");
      }
      sb.append(">\n<head>");
      String defaultTitle = null;
      for (Element element : shell.getElementsByTag("title")) {
        if (config.includeInShell(element)) {
          defaultTitle = element.text();
        }
      }
      if (defaultTitle != null) {
        sb.append("<title>").append(defaultTitle).append("</title>");
      }
      for (Element element : shell.getElementsByTag("meta")) {
        if (config.includeInShell(element)) {
          sb.append(element);
        }
      }
      for (Element element : shell.getElementsByTag("link")) {
        if (config.includeInShell(element)) {
          sb.append(element);
        }
      }
      for (Element element : shell.getElementsByTag("script")) {
        if (config.includeInShell(element)) {
          scripts.append(element);
        }
      }
    } else {
      sb.append("<!DOCTYPE html>\n<html>\n<head>");
    }
    if (config.useLocalAdamaJavascript) {
      sb.append("<script src=\"/" + System.currentTimeMillis() + "/devlibadama.js\"></script>");
    } else {
      sb.append("<script src=\"/libadama.js/"+ config.version +".js\"></script>");
    }
    sb.append("<script>\n\n").append(javascript).append("\n\n</script>");
    sb.append("<style>\n\n").append(style).append("\n\n</style>");
    sb.append(scripts);
    sb.append("</head>");
    if (shell != null) {
      sb.append("<body");
      if (shell.hasAttr("body-class")) {
        sb.append(" class=\"").append(shell.attr("body-class")).append("\"");
      }
      sb.append(">");
    } else {
      sb.append("<body>");
    }
    sb.append("</body><script>\n");
    sb.append("  RxHTML.init();\n");
    if (worker) {
      if (config.useLocalAdamaJavascript) {
        sb.append("  RxHTML.worker(\""+workerIdentity+"\",\"/" + Platform.JS_VERSION + "/devlibadama-worker.js\",'").append(Platform.JS_VERSION).append("');\n");
      } else {
        sb.append("  RxHTML.worker(\""+workerIdentity+"\",\"/libadama-worker.js/" + Platform.JS_VERSION + ".js\",'").append(Platform.JS_VERSION).append("');\n");
      }
    }
    sb.append("</script></html>");
    return sb.toString();
  }
}
