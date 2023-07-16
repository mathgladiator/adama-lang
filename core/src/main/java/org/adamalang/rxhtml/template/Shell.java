/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import org.adamalang.rxhtml.Feedback;
import org.adamalang.rxhtml.RxHtmlResult;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/** a class for pulling out the first and (hopefully) only shell */
public class Shell {
  private final Feedback feedback;
  private Element shell;

  public Shell(Feedback feedback) {
    this.feedback = feedback;
    this.shell = null;
  }

  public void scan(Document document) {
    for (Element element : document.getElementsByTag("shell")) {
      if (this.shell == null) {
        this.shell = element;
      } else {
        feedback.warn(element, "A duplicate shell was found");
      }
    }
  }

  public String makeShell(RxHtmlResult result) {
    StringBuilder sb = new StringBuilder();
    StringBuilder scripts = new StringBuilder();
    if (shell != null) {
      sb.append("<!DOCTYPE html>\n<html");
      if (shell.hasAttr("html-class")) {
        sb.append(" class=\"").append(shell.attr("html-class")).append("\"");
      }
      sb.append(">\n<head>");
      String defaultTitle = null;
      for (Element element : shell.getElementsByTag("title")) {
        defaultTitle = element.text();
      }
      if (defaultTitle != null) {
        sb.append("<title>").append(defaultTitle).append("</title>");
      }
      for (Element element : shell.getElementsByTag("meta")) {
        sb.append(element.toString());
      }
      // TODO: think about scripts
      for (Element element : shell.getElementsByTag("link")) {
        sb.append(element.toString());
      }
      for (Element element : shell.getElementsByTag("script")) {
        scripts.append(element.toString());
      }
    } else {
      sb.append("<!DOCTYPE html>\n<html>\n<head>");
    }
    sb.append("<script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script>");
    sb.append("<script>\n\n").append(result.javascript).append("\n\n</script>");
    sb.append("<style>\n\n").append(result.style).append("\n\n</style>");
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
    sb.append("</body><script>");
    sb.append("RxHTML.init();");
    sb.append("</script></html>");
    return sb.toString();
  }
}
