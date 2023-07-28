/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateWrapSimpleTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    var g=$.RX([]);");
    gold.append("\n    $.WP(c,a,'custom_wrapper',g,function(e,d,f) {");
    gold.append("\n      if (f == 'a') {");
    gold.append("\n        var h = $.E('div');");
    gold.append("\n        h.append($.T(' A '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n      if (f == 'b') {");
    gold.append("\n        var h = $.E('div');");
    gold.append("\n        h.append($.T(' B '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n      if (f == 'c') {");
    gold.append("\n        var h = $.E('div');");
    gold.append("\n        h.append($.T(' C '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n      if (f == 'd') {");
    gold.append("\n        var h = $.E('div');");
    gold.append("\n        h.append($.T(' D '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n    });");
    gold.append("\n    g.__();");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    var g=$.RX([]);");
    gold.append("\n    $.WP(c,a,'custom_wrapper',g,function(e,d,f) {");
    gold.append("\n      if (f == 'a') {");
    gold.append("\n        var h = $.E('div');");
    gold.append("\n        h.append($.T(' A '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n      if (f == 'b') {");
    gold.append("\n        var h = $.E('div');");
    gold.append("\n        h.append($.T(' B '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n      if (f == 'c') {");
    gold.append("\n        var h = $.E('div');");
    gold.append("\n        h.append($.T(' C '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n      if (f == 'd') {");
    gold.append("\n        var h = $.E('div');");
    gold.append("\n        h.append($.T(' D '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n    });");
    gold.append("\n    g.__();");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</script><style>");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</style></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <div rx:wrap=\"custom_wrapper\">");
    source.append("\n            <div rx:case=\"a\">");
    source.append("\n                A");
    source.append("\n            </div>");
    source.append("\n            <div rx:case=\"b\">");
    source.append("\n                B");
    source.append("\n            </div>");
    source.append("\n            <div rx:case=\"c\">");
    source.append("\n                C");
    source.append("\n            </div>");
    source.append("\n            <div rx:case=\"d\">");
    source.append("\n                D");
    source.append("\n            </div>");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
