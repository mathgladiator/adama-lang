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

public class TemplateSetTitleTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c={};");
    gold.append("\n    c._={};");
    gold.append("\n    c._.value={};");
    gold.append("\n    c._._ = function() {");
    gold.append("\n      c.value=c._.value['val'] + \" - YO\"");
    gold.append("\n      c.__();");
    gold.append("\n    }");
    gold.append("\n    $.Y(a,c._.value,'val', c._._);");
    gold.append("\n    $.ST(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <title value=\"{val} - YO\" />");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
