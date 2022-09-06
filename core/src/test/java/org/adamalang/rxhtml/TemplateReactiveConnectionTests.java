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

public class TemplateReactiveConnectionTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c=$.RX(['space','key']);");
    gold.append("\n    c.name='myname';");
    gold.append("\n    $.Y2(a,c,'space','suffix',function(d) {");
    gold.append("\n      c.space=\"space\" + d['suffix']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2($.pR($.pV(a)),c,'key','key',function(d) {");
    gold.append("\n      c.key=d['key']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    c.identity=true;");
    gold.append("\n    $.CONNECT(a,c,'/sign-in');");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    var f=$.E('div');");
    gold.append("\n    b.append(f);");
    gold.append("\n    $.P(f,a,d,function(f,e) {");
    gold.append("\n    },function(f,e) {");
    gold.append("\n    });");
    gold.append("\n    d.__();");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='myname';");
    gold.append("\n    var h=$.E('div');");
    gold.append("\n    b.append(h);");
    gold.append("\n    $.P(h,a,e,function(h,g) {");
    gold.append("\n    },function(h,g) {");
    gold.append("\n    });");
    gold.append("\n    e.__();");
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
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <connection name=\"myname\" space=\"space{suffix}\" key=\"{view:/key}\">");
    source.append("\n");
    source.append("\n        </connection>");
    source.append("\n        <pick name=\"myname\">");
    source.append("\n");
    source.append("\n        </pick>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
