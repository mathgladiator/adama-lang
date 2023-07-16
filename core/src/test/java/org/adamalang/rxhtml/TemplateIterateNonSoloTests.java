/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateIterateNonSoloTests extends BaseRxHtmlTest {
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
    gold.append("\n    $.IT(c,$.pD(a),'set',false,function(d) {");
    gold.append("\n      var e = $.E('div');");
    gold.append("\n      e.append($.L(d,'key'));");
    gold.append("\n      e.append($.L(d,'value'));");
    gold.append("\n      return e;");
    gold.append("\n    });");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('table');");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    $.IT(c,$.pD(a),'set',true,function(d) {");
    gold.append("\n      var e = $.E('div');");
    gold.append("\n      var f = $.E('span');");
    gold.append("\n      f.append($.L(d,'key'));");
    gold.append("\n      f.append($.T(' - '));");
    gold.append("\n      f.append($.L(d,'value'));");
    gold.append("\n      e.append(f);");
    gold.append("\n      return e;");
    gold.append("\n    });");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('table');");
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
    gold.append("\n    $.IT(c,$.pD(a),'set',false,function(d) {");
    gold.append("\n      var e = $.E('div');");
    gold.append("\n      e.append($.L(d,'key'));");
    gold.append("\n      e.append($.L(d,'value'));");
    gold.append("\n      return e;");
    gold.append("\n    });");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('table');");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    $.IT(c,$.pD(a),'set',true,function(d) {");
    gold.append("\n      var e = $.E('div');");
    gold.append("\n      var f = $.E('span');");
    gold.append("\n      f.append($.L(d,'key'));");
    gold.append("\n      f.append($.T(' - '));");
    gold.append("\n      f.append($.L(d,'value'));");
    gold.append("\n      e.append(f);");
    gold.append("\n      return e;");
    gold.append("\n    });");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('table');");
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
    source.append("\n        <table>");
    source.append("\n            <div rx:iterate=\"data:set\">");
    source.append("\n                <lookup path=\"key\" />");
    source.append("\n                <lookup path=\"value\" />");
    source.append("\n            </div>");
    source.append("\n        </table>");
    source.append("\n        <table>");
    source.append("\n            <div rx:iterate=\"data:set\" rx:expand-view-state>");
    source.append("\n                [<span> <lookup path=\"key\" /> - <lookup path=\"value\" /> </span>]");
    source.append("\n            </div>");
    source.append("\n        </table>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
