/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <connection name=\"myname\" use-domain=\"\" redirect=\"/sign-in\">");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    d.identity=true;");
    gold.append("\n    d.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,d);");
    gold.append("\n    d.__();");
    gold.append("\n");
    gold.append("\n    // <connection name=\"myname\" use-domain=\"\" redirect=\"/sign-in\">");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='myname';");
    gold.append("\n    $.P(b,a,e,function(b,f) {");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" rx:sync=\"value_a\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      b.append(g);");
    gold.append("\n      $.SY(g,$.pV(f),'value_a',100.0);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" rx:sync=\"value_b\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      b.append(g);");
    gold.append("\n      $.SY(g,$.pV(f),'value_b',100.0);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" disabled=\"{view:disable}\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      {");
    gold.append("\n        var h={};");
    gold.append("\n        h.__dom=g;");
    gold.append("\n        var i=(function() {");
    gold.append("\n          $.FV(this.__dom,'disabled',$.B($.F(this,'disable')));");
    gold.append("\n        }).bind(h);");
    gold.append("\n        $.Y($.pV(f),h,'disable',i);");
    gold.append("\n        i();");
    gold.append("\n      }");
    gold.append("\n      b.append(g);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" disabled=\"true\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      $.FV(g,'disabled',true);");
    gold.append("\n      b.append(g);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" disabled=\"false\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      $.FV(g,'disabled',false);");
    gold.append("\n      b.append(g);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" disabled=\"{view:disable}\" required=\"{view:required}\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      {");
    gold.append("\n        var h={};");
    gold.append("\n        h.__dom=g;");
    gold.append("\n        var i=(function() {");
    gold.append("\n          $.FV(this.__dom,'disabled',$.B($.F(this,'disable')));");
    gold.append("\n        }).bind(h);");
    gold.append("\n        $.Y($.pV(f),h,'disable',i);");
    gold.append("\n        i();");
    gold.append("\n      }");
    gold.append("\n      {");
    gold.append("\n        var h={};");
    gold.append("\n        h.__dom=g;");
    gold.append("\n        var i=(function() {");
    gold.append("\n          $.FV(this.__dom,'required',$.B($.F(this,'required')));");
    gold.append("\n        }).bind(h);");
    gold.append("\n        $.Y($.pV(f),h,'required',i);");
    gold.append("\n        i();");
    gold.append("\n      }");
    gold.append("\n      b.append(g);");
    gold.append("\n");
    gold.append("\n      // <view-write name=\"sum\" value=\"{value_a}_{value_b}\">");
    gold.append("\n      var g=$.RX(['value']);");
    gold.append("\n      $.Y2(f,g,'value','value_a',function(h) {");
    gold.append("\n        g.value=$.F(h,'value_a') + \"_\" + $.F(h,'value_b')");
    gold.append("\n        g.__();");
    gold.append("\n      });");
    gold.append("\n      $.Y2(f,g,'value','value_b',function(h) {");
    gold.append("\n        g.value=$.F(h,'value_a') + \"_\" + $.F(h,'value_b')");
    gold.append("\n        g.__();");
    gold.append("\n      });");
    gold.append("\n      $.VW($.pV(f),'',g);");
    gold.append("\n    },function(b,f) {");
    gold.append("\n    },false);");
    gold.append("\n    e.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <connection name=\"myname\" use-domain=\"\" redirect=\"/sign-in\">");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    d.identity=true;");
    gold.append("\n    d.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,d);");
    gold.append("\n    d.__();");
    gold.append("\n");
    gold.append("\n    // <connection name=\"myname\" use-domain=\"\" redirect=\"/sign-in\">");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='myname';");
    gold.append("\n    $.P(b,a,e,function(b,f) {");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" rx:sync=\"value_a\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      b.append(g);");
    gold.append("\n      $.SY(g,$.pV(f),'value_a',100.0);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" rx:sync=\"value_b\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      b.append(g);");
    gold.append("\n      $.SY(g,$.pV(f),'value_b',100.0);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" disabled=\"{view:disable}\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      {");
    gold.append("\n        var h={};");
    gold.append("\n        h.__dom=g;");
    gold.append("\n        var i=(function() {");
    gold.append("\n          $.FV(this.__dom,'disabled',$.B($.F(this,'disable')));");
    gold.append("\n        }).bind(h);");
    gold.append("\n        $.Y($.pV(f),h,'disable',i);");
    gold.append("\n        i();");
    gold.append("\n      }");
    gold.append("\n      b.append(g);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" disabled=\"true\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      $.FV(g,'disabled',true);");
    gold.append("\n      b.append(g);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" disabled=\"false\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      $.FV(g,'disabled',false);");
    gold.append("\n      b.append(g);");
    gold.append("\n");
    gold.append("\n      // <input type=\"text\" disabled=\"{view:disable}\" required=\"{view:required}\">");
    gold.append("\n      var g=$.E('input');");
    gold.append("\n      $.SA(g,'type',\"text\");");
    gold.append("\n      {");
    gold.append("\n        var h={};");
    gold.append("\n        h.__dom=g;");
    gold.append("\n        var i=(function() {");
    gold.append("\n          $.FV(this.__dom,'disabled',$.B($.F(this,'disable')));");
    gold.append("\n        }).bind(h);");
    gold.append("\n        $.Y($.pV(f),h,'disable',i);");
    gold.append("\n        i();");
    gold.append("\n      }");
    gold.append("\n      {");
    gold.append("\n        var h={};");
    gold.append("\n        h.__dom=g;");
    gold.append("\n        var i=(function() {");
    gold.append("\n          $.FV(this.__dom,'required',$.B($.F(this,'required')));");
    gold.append("\n        }).bind(h);");
    gold.append("\n        $.Y($.pV(f),h,'required',i);");
    gold.append("\n        i();");
    gold.append("\n      }");
    gold.append("\n      b.append(g);");
    gold.append("\n");
    gold.append("\n      // <view-write name=\"sum\" value=\"{value_a}_{value_b}\">");
    gold.append("\n      var g=$.RX(['value']);");
    gold.append("\n      $.Y2(f,g,'value','value_a',function(h) {");
    gold.append("\n        g.value=$.F(h,'value_a') + \"_\" + $.F(h,'value_b')");
    gold.append("\n        g.__();");
    gold.append("\n      });");
    gold.append("\n      $.Y2(f,g,'value','value_b',function(h) {");
    gold.append("\n        g.value=$.F(h,'value_a') + \"_\" + $.F(h,'value_b')");
    gold.append("\n        g.__();");
    gold.append("\n      });");
    gold.append("\n      $.VW($.pV(f),'',g);");
    gold.append("\n    },function(b,f) {");
    gold.append("\n    },false);");
    gold.append("\n    e.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</script><style>");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</style></head><body></body><script>");
    gold.append("\n  RxHTML.init();");
    gold.append("\n</script></html>");
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
    source.append("\n            <input type=\"text\" disabled=\"true\" />");
    source.append("\n            <input type=\"text\" disabled=\"false\" />");
    source.append("\n            <input type=\"text\" disabled=\"{view:disable}\" required=\"{view:required}\"/>");
    source.append("\n            <view-write name=\"sum\" value=\"{value_a}_{value_b}\" />");
    source.append("\n        </connection>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/\" : {");
    gold.append("\n    \"disable\" : \"value\",");
    gold.append("\n    \"required\" : \"value\"");
    gold.append("\n  }");
    gold.append("\n}");
    return gold.toString();
  }
}
