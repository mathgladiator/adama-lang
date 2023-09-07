/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateCommandOrderToggleTests extends BaseRxHtmlTest {
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
    gold.append("\n    $.onOT(c,'click',$.pV(a),'people_sort_by','age');");
    gold.append("\n    c.append($.T('By Age'));");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    $.onOT(c,'click',$.pV(a),'people_sort_by','name');");
    gold.append("\n    c.append($.T('By Name'));");
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
    gold.append("\n    $.onOT(c,'click',$.pV(a),'people_sort_by','age');");
    gold.append("\n    c.append($.T('By Age'));");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('button');");
    gold.append("\n    $.onOT(c,'click',$.pV(a),'people_sort_by','name');");
    gold.append("\n    c.append($.T('By Name'));");
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
    source.append("\n        <button rx:click=\"ot:people_sort_by=age\">By Age</button>");
    source.append("\n        <button rx:click=\"ot:people_sort_by=name\">By Name</button>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
