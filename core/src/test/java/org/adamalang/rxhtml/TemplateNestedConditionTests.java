/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateNestedConditionTests extends BaseRxHtmlTest {
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
    gold.append("\n  $.PG(['fixed','nest'], function(b,a) {");
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        $.ACLASS(this.__dom,((this['x']) ? (\" X is true \" + ((this['y']) ? (\" Y=true \") : (\" Y=false \"))) : (\" X is false \")));");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'x',e);");
    gold.append("\n      $.Y(a,d,'y',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    c.append($.T(' Well, something complex... '));");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed','nest'], function(b,a) {");
    gold.append("\n    var c = $.E('div');");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        $.ACLASS(this.__dom,((this['x']) ? (\" X is true \" + ((this['y']) ? (\" Y=true \") : (\" Y=false \"))) : (\" X is false \")));");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'x',e);");
    gold.append("\n      $.Y(a,d,'y',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    c.append($.T(' Well, something complex... '));");
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
    source.append("\n    <page uri=\"/nest\">");
    source.append("\n        <div class=\"[x]X is true[y]Y=true[#]Y=false[/][#]X is false [/]\">");
    source.append("\n            Well, something complex...");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
