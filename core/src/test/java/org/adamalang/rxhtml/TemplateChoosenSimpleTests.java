/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateChoosenSimpleTests extends BaseRxHtmlTest {
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
    gold.append("\n    c.redirect='/sign-in';");
    gold.append("\n    $.CONNECT(a,c);");
    gold.append("\n    c.__();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='default';");
    gold.append("\n    $.P(b,a,d,function(b,e) {");
    gold.append("\n      var f = $.E('div');");
    gold.append("\n      $.DE(f,e,e,'channel','id','id',true,false,function(h,g) {");
    gold.append("\n        var i = $.E('button');");
    gold.append("\n        $.exCH(i,'click',g,'id','channel','id');");
    gold.append("\n        var k = $.E('span');");
    gold.append("\n        $.CSEN(k,g,g,'channel','id','id',true,false,function(m,l) {");
    gold.append("\n          m.append($.T(' Add Choice '));");
    gold.append("\n        },function(m,l) {");
    gold.append("\n          var n = $.E('span');");
    gold.append("\n          n.append($.T('Remove Choice'));");
    gold.append("\n          m.append(n);");
    gold.append("\n        });");
    gold.append("\n        i.append(k);");
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
    source.append("\n                <button rx:click=\"choose:channel\">");
    source.append("\n                    <span rx:if=\"chosen:channel\">");
    source.append("\n                        Add Choice");
    source.append("\n                        <span rx:else>Remove Choice</span>");
    source.append("\n                    </span>");
    source.append("\n                </button>");
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
