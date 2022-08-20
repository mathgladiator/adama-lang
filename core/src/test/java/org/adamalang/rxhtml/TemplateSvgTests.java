/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

public class TemplateSvgTests extends BaseRxHtmlTest {
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('svg', 'http://www.w3.org/2000/svg');");
    gold.append("\n    c.setAttribute('height','210');");
    gold.append("\n    c.setAttribute('width','400');");
    gold.append("\n    var d = $.E('path', 'http://www.w3.org/2000/svg');");
    gold.append("\n    d.setAttribute('d','M150 0 L75 200 L225 200 Z');");
    gold.append("\n    c.append(d);");
    gold.append("\n    c.append($.T(' Sorry, your browser does not support inline SVG. '));");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script src=\"https://aws-us-east-2.adama-platform.com/rxhtml.js\"></script><link rel=\"stylesheet\" href=\"/template.css\"><script src=\"/template.js\"></script></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n");
    source.append("\n        <svg height=\"210\" width=\"400\">");
    source.append("\n            <path d=\"M150 0 L75 200 L225 200 Z\" />");
    source.append("\n            Sorry, your browser does not support inline SVG.");
    source.append("\n        </svg>");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
