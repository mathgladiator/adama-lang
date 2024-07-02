/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

public class TemplateReplicateOptionTests extends BaseRxHtmlTest {
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
    gold.append("\n    // <select>");
    gold.append("\n    var d=$.E('select');");
    gold.append("\n");
    gold.append("\n    // <option>");
    gold.append("\n    var e=$.E('option');");
    gold.append("\n    e.append($.T('Some option'));");
    gold.append("\n    d.append(e);");
    gold.append("\n    $.IIT(d,e,a,'example_1',false,function(f) {");
    gold.append("\n");
    gold.append("\n      // <option value=\"{name}\">");
    gold.append("\n      var g=$.E('option');");
    gold.append("\n      {");
    gold.append("\n        var h={};");
    gold.append("\n        h.__dom=g;");
    gold.append("\n        var i=(function() {");
    gold.append("\n          $.SV(this.__dom,$.F(this,'name'));");
    gold.append("\n        }).bind(h);");
    gold.append("\n        $.Y(f,h,'name',i);");
    gold.append("\n        i();");
    gold.append("\n      }");
    gold.append("\n      return g;");
    gold.append("\n    });");
    gold.append("\n");
    gold.append("\n    // <option>");
    gold.append("\n    var f=$.E('option');");
    gold.append("\n    f.append($.T('Another option'));");
    gold.append("\n    d.append(f);");
    gold.append("\n    b.append(d);");
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
    gold.append("\n    // <select>");
    gold.append("\n    var d=$.E('select');");
    gold.append("\n");
    gold.append("\n    // <option>");
    gold.append("\n    var e=$.E('option');");
    gold.append("\n    e.append($.T('Some option'));");
    gold.append("\n    d.append(e);");
    gold.append("\n    $.IIT(d,e,a,'example_1',false,function(f) {");
    gold.append("\n");
    gold.append("\n      // <option value=\"{name}\">");
    gold.append("\n      var g=$.E('option');");
    gold.append("\n      {");
    gold.append("\n        var h={};");
    gold.append("\n        h.__dom=g;");
    gold.append("\n        var i=(function() {");
    gold.append("\n          $.SV(this.__dom,$.F(this,'name'));");
    gold.append("\n        }).bind(h);");
    gold.append("\n        $.Y(f,h,'name',i);");
    gold.append("\n        i();");
    gold.append("\n      }");
    gold.append("\n      return g;");
    gold.append("\n    });");
    gold.append("\n");
    gold.append("\n    // <option>");
    gold.append("\n    var f=$.E('option');");
    gold.append("\n    f.append($.T('Another option'));");
    gold.append("\n    d.append(f);");
    gold.append("\n    b.append(d);");
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
    source.append("\n        <select>");
    source.append("\n            <option>Some option</option>");
    source.append("\n            <option rx:replicate=\"example_1\" value=\"{name}\"></option>");
    source.append("\n            <option>Another option</option>");
    source.append("\n        </select>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/\" : { }");
    gold.append("\n}");
    return gold.toString();
  }
}
