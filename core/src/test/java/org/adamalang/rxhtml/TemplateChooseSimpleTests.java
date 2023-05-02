/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

public class TemplateChooseSimpleTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c=$.RX([]);");
    gold.append("\n    c.name='default';");
    gold.append("\n    c.space='space';");
    gold.append("\n    c.key='key';");
    gold.append("\n    c.identity=true;");
    gold.append("\n    $.CONNECT(a,c,'/sign-in');");
    gold.append("\n    c.__();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='default';");
    gold.append("\n    $.P(b,a,d,function(b,e) {");
    gold.append("\n      var f = $.E('div');");
    gold.append("\n      $.DE(f,e,e,'channel','id','id',true,false,function(h,g) {");
    gold.append("\n        var i = $.E('button');");
    gold.append("\n        $.exCH(i,'click',g,'id','channel','id');");
    gold.append("\n        i.append($.T('Add Choice'));");
    gold.append("\n        h.append(i);");
    gold.append("\n      },function(h,g) {");
    gold.append("\n      });");
    gold.append("\n      b.append(f);");
    gold.append("\n      var f = $.E('div');");
    gold.append("\n      $.FIN(f,e,'channel',true,false,function(h,g) {");
    gold.append("\n        var i = $.E('button');");
    gold.append("\n        $.exFIN(i,'click',g,'channel');");
    gold.append("\n        i.append($.T('Send choices'));");
    gold.append("\n        h.append(i);");
    gold.append("\n        h.append($.T(' Time to decide! '));");
    gold.append("\n      },function(h,g) {");
    gold.append("\n      });");
    gold.append("\n      b.append(f);");
    gold.append("\n    },function(b,e) {");
    gold.append("\n    });");
    gold.append("\n    d.__();");
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
    source.append("\n<page uri=\"/\">");
    source.append("\n        <connection space=\"space\" key=\"key\">");
    source.append("\n            <div rx:if=\"choose:channel\">");
    source.append("\n                <button rx:click=\"choose:channel\">Add Choice</button>");
    source.append("\n            </div>");
    source.append("\n            <div rx:if=\"finalize:channel\">");
    source.append("\n                <button rx:click=\"finalize:channel\">Send choices</button>");
    source.append("\n                Time to decide!");
    source.append("\n            </div>");
    source.append("\n        </connection>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
