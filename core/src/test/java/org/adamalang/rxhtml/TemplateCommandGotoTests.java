/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateCommandGotoTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
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
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    var e = {};");
    gold.append("\n    $.YS($.pV(a),e,'here');");
    gold.append("\n    $.onGO(c,'click',a,function(){ return \"/\" + e['here'];});");
    gold.append("\n    c.append($.T('goto View'));");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    var g = {};");
    gold.append("\n    $.YS($.pD(a),g,'there');");
    gold.append("\n    $.onGO(c,'click',a,function(){ return \"/path-to-data/\" + g['there'];});");
    gold.append("\n    c.append($.T('goto Data'));");
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
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    var e = {};");
    gold.append("\n    $.YS($.pV(a),e,'here');");
    gold.append("\n    $.onGO(c,'click',a,function(){ return \"/\" + e['here'];});");
    gold.append("\n    c.append($.T('goto View'));");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    var g = {};");
    gold.append("\n    $.YS($.pD(a),g,'there');");
    gold.append("\n    $.onGO(c,'click',a,function(){ return \"/path-to-data/\" + g['there'];});");
    gold.append("\n    c.append($.T('goto Data'));");
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
    source.append("\n        <button rx:click=\"goto:/{view:here}\">goto View</button>");
    source.append("\n        <button rx:click=\"goto:/path-to-data/{data:there}\">goto Data</button>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
