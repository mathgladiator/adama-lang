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

public class TemplateIfSimpleTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    $.IF(c,a,'obj', true,false,function(e,d) {");
    gold.append("\n      e.append($.L(d,'key'));");
    gold.append("\n      e.append($.T(' - '));");
    gold.append("\n      e.append($.L(d,'value'));");
    gold.append("\n    }, function(e,d) {");
    gold.append("\n      var f = $.E('div');");
    gold.append("\n      f.append($.T(' Not set! '));");
    gold.append("\n      e.append(f);");
    gold.append("\n    });");
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
    source.append("\n        <div rx:if=\"obj\">");
    source.append("\n            <lookup path=\"key\" /> - <lookup path=\"value\" />");
    source.append("\n            <div rx:else>");
    source.append("\n                Not set!");
    source.append("\n            </div>");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
