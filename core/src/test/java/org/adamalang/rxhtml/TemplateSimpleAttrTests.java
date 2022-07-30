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

public class TemplateSimpleAttrTests extends BaseRxHtmlTest {
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
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        this.__dom.setAttribute('class',this['classy'] + \" \" + ((this['b']) ? (\"active\") : (\"inactive\")));");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'b',e);");
    gold.append("\n      $.Y(a,d,'classy',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    c.setAttribute('fixed','constant string');");
    gold.append("\n    c.setAttribute('len','40');");
    gold.append("\n    c.append($.T(' Yaz '));");
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
    source.append("\n        Simple Page");
    source.append("\n        <div class=\"{classy} [b]active[#b]inactive[/b]\" fixed=\"constant string\" len=40>");
    source.append("\n            Yaz");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
