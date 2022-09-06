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

public class TemplateTemplatesTests extends BaseRxHtmlTest {
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
    gold.append("\n  $.TP('temp', function(a,b,c) {");
    gold.append("\n    a.append($.T(' This is a template with a fragment: '));");
    gold.append("\n    c(a,b,'');");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    b.append($.T(' This is a page which is going to use the template. '));");
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    $.UT(c,a,'temp', function(d,e,f) {");
    gold.append("\n      d.append($.T(' This is data within the template. '));");
    gold.append("\n    });");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><link rel=\"stylesheet\" href=\"/template.css\"><script src=\"/template.js\"></script></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <template name=\"temp\">");
    source.append("\n        This is a template with a fragment: <fragment />");
    source.append("\n    </template>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        This is a page which is going to use the template.");
    source.append("\n        <div rx:template=\"temp\">");
    source.append("\n            This is data within the template.");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
