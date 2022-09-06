/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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

  public String makeShell(RxHtmlResult result, boolean inline) {
    StringBuilder sb = new StringBuilder();
    sb.append("<!DOCTYPE html>\n<html>\n<head>");

    if (shell != null) {
      inline = shell.hasAttr("inline");
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
    }
    sb.append("<script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script>");
    sb.append("<script src=\"https://aws-us-east-2.adama-platform.com/rxhtml.js\"></script>");
    if (inline) {
      sb.append("<script>\n\n").append(result.javascript).append("\n\n</script>");
      sb.append("<style>\n\n").append(result.style).append("\n\n</style>");
    } else {
      sb.append("<link rel=\"stylesheet\" href=\"/template.css\">");
      sb.append("<script src=\"/template.js\"></script>");
    }
    sb.append("</head>");
    sb.append("<body></body><script>");
    sb.append("RxHTML.init();");
    sb.append("</script></html>");
    return sb.toString();
  }
}
