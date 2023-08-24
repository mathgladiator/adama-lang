/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateViewWriteTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c=$.RX([]);");
    gold.append("\n    c.name='myname';");
    gold.append("\n    c.identity=true;");
    gold.append("\n    c.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,c);");
    gold.append("\n    c.__();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    $.P(b,a,d,function(b,e) {");
    gold.append("\n      var f = $.E('input');");
    gold.append("\n      $.SA(f,'type',\"text\");");
    gold.append("\n      b.append(f);");
    gold.append("\n      $.SY(f,$.pV(e),'value_a',100.0);");
    gold.append("\n      var f = $.E('input');");
    gold.append("\n      $.SA(f,'type',\"text\");");
    gold.append("\n      b.append(f);");
    gold.append("\n      $.SY(f,$.pV(e),'value_b',100.0);");
    gold.append("\n      var f = $.E('input');");
    gold.append("\n      $.SA(f,'type',\"text\");");
    gold.append("\n      {");
    gold.append("\n        var g = {};");
    gold.append("\n        g.__dom = f;");
    gold.append("\n        var h = (function() {");
    gold.append("\n          this.__dom.value='true' == (this['disable']);");
    gold.append("\n        }).bind(g);");
    gold.append("\n        $.Y($.pV(e),g,'disable',h);");
    gold.append("\n        h();");
    gold.append("\n      }");
    gold.append("\n      b.append(f);");
    gold.append("\n      var f=$.RX(['value']);");
    gold.append("\n      $.Y2(e,f,'value','value_a',function(g) {");
    gold.append("\n        f.value=g['value_a'] + \"_\" + g['value_b']");
    gold.append("\n        f.__();");
    gold.append("\n      });");
    gold.append("\n      $.Y2(e,f,'value','value_b',function(g) {");
    gold.append("\n        f.value=g['value_a'] + \"_\" + g['value_b']");
    gold.append("\n        f.__();");
    gold.append("\n      });");
    gold.append("\n      $.VW($.pV(e),'',f);");
    gold.append("\n    },function(b,e) {");
    gold.append("\n    });");
    gold.append("\n    d.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.RX([]);");
    gold.append("\n    c.name='myname';");
    gold.append("\n    c.identity=true;");
    gold.append("\n    c.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,c);");
    gold.append("\n    c.__();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    $.P(b,a,d,function(b,e) {");
    gold.append("\n      var f = $.E('input');");
    gold.append("\n      $.SA(f,'type',\"text\");");
    gold.append("\n      b.append(f);");
    gold.append("\n      $.SY(f,$.pV(e),'value_a',100.0);");
    gold.append("\n      var f = $.E('input');");
    gold.append("\n      $.SA(f,'type',\"text\");");
    gold.append("\n      b.append(f);");
    gold.append("\n      $.SY(f,$.pV(e),'value_b',100.0);");
    gold.append("\n      var f = $.E('input');");
    gold.append("\n      $.SA(f,'type',\"text\");");
    gold.append("\n      {");
    gold.append("\n        var g = {};");
    gold.append("\n        g.__dom = f;");
    gold.append("\n        var h = (function() {");
    gold.append("\n          this.__dom.value='true' == (this['disable']);");
    gold.append("\n        }).bind(g);");
    gold.append("\n        $.Y($.pV(e),g,'disable',h);");
    gold.append("\n        h();");
    gold.append("\n      }");
    gold.append("\n      b.append(f);");
    gold.append("\n      var f=$.RX(['value']);");
    gold.append("\n      $.Y2(e,f,'value','value_a',function(g) {");
    gold.append("\n        f.value=g['value_a'] + \"_\" + g['value_b']");
    gold.append("\n        f.__();");
    gold.append("\n      });");
    gold.append("\n      $.Y2(e,f,'value','value_b',function(g) {");
    gold.append("\n        f.value=g['value_a'] + \"_\" + g['value_b']");
    gold.append("\n        f.__();");
    gold.append("\n      });");
    gold.append("\n      $.VW($.pV(e),'',f);");
    gold.append("\n    },function(b,e) {");
    gold.append("\n    });");
    gold.append("\n    d.__();");
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
    source.append("\n        <connection name=\"myname\" use-domain>");
    source.append("\n            <input type=\"text\" rx:sync=\"value_a\" />");
    source.append("\n            <input type=\"text\" rx:sync=\"value_b\" />");
    source.append("\n            <input type=\"text\" disabled=\"{view:disable}\" />");
    source.append("\n            <view-write name=\"sum\" value=\"{value_a}_{value_b}\" />");
    source.append("\n        </connection>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
