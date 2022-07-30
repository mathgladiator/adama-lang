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

public class TemplateIterateSimpleTests extends BaseRxHtmlTest {
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('table');");
    gold.append("\n    var d = $.E('tbody');");
    gold.append("\n    $.IT(d,$.pD(a),'set',false,function(e) {");
    gold.append("\n      var f = $.E('tr');");
    gold.append("\n      var g = $.E('td');");
    gold.append("\n      g.append($.L(e,'key'));");
    gold.append("\n      g.append($.L(e,'value'));");
    gold.append("\n      f.append(g);");
    gold.append("\n      return f;");
    gold.append("\n    });");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('table');");
    gold.append("\n    var d = $.E('tbody');");
    gold.append("\n    $.IT(d,$.pD(a),'set',true,function(e) {");
    gold.append("\n      var f = $.E('tr');");
    gold.append("\n      var g = $.E('td');");
    gold.append("\n      g.append($.L(e,'key'));");
    gold.append("\n      g.append($.L(e,'value'));");
    gold.append("\n      f.append(g);");
    gold.append("\n      return f;");
    gold.append("\n    });");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <table>");
    source.append("\n            <tbody rx:iterate=\"data:set\">");
    source.append("\n            <tr><td>");
    source.append("\n                <lookup path=\"key\" />");
    source.append("\n                <lookup path=\"value\" />");
    source.append("\n            </td></tr>");
    source.append("\n            </tbody>");
    source.append("\n        </table>");
    source.append("\n        <table>");
    source.append("\n            <tbody rx:iterate=\"data:set\" rx:expand-view-state>");
    source.append("\n            <tr><td>");
    source.append("\n                <lookup path=\"key\" />");
    source.append("\n                <lookup path=\"value\" />");
    source.append("\n            </td></tr>");
    source.append("\n            </tbody>");
    source.append("\n        </table>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
