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

public class TemplateReactiveCustomDataTests extends BaseRxHtmlTest {
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
    gold.append("\n    c.one='one';");
    gold.append("\n    c._={};");
    gold.append("\n    c._.two={};");
    gold.append("\n    c._._ = function() {");
    gold.append("\n      c.two=c._.two['var']");
    gold.append("\n      c.__();");
    gold.append("\n    }");
    gold.append("\n    $.Y(a,c._.two,'var', c._._);");
    gold.append("\n    c._.three={};");
    gold.append("\n    c._._ = function() {");
    gold.append("\n      c.three=c._.three['var1'] + c._.three['var2']");
    gold.append("\n      c.__();");
    gold.append("\n    }");
    gold.append("\n    $.Y(a,c._.three,'var1', c._._);");
    gold.append("\n    $.Y(a,c._.three,'var2', c._._);");
    gold.append("\n    c.present='';");
    gold.append("\n    $.CUDA(b,a,'',c,'/sign-in',function(d) {");
    gold.append("\n    });");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <customdata parameter:one=\"one\" parameter:two=\"{var}\"  parameter:three=\"{var1}{var2}\" parameter:present>");
    source.append("\n");
    source.append("\n        </customdata>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
