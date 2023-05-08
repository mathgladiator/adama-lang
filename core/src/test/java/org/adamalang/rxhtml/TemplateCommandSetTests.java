/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

public class TemplateCommandSetTests extends BaseRxHtmlTest {
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
    gold.append("\n    $.onS(c,'click',$.pV(a),'x',function(){ return e['here'];});");
    gold.append("\n    c.append($.T('Set View'));");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    var g = {};");
    gold.append("\n    $.YS($.pD(a),g,'there');");
    gold.append("\n    $.onS(c,'click',$.pV(a),'x',function(){ return g['there'];});");
    gold.append("\n    $.onS(c,'click',$.pV(a),'y','The Big Long Title');");
    gold.append("\n    c.append($.T('Set Data'));");
    gold.append("\n    b.append(c);");
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
    source.append("\n        <button rx:click=\"set:x={view:here}\">Set View</button>");
    source.append("\n        <button rx:click=\"set:x={data:there} set:y='The Big Long Title'\">Set Data</button>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
