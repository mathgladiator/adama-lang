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
    gold.append("\n    var c=$.X();");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    {");
    gold.append("\n      var e={};");
    gold.append("\n      e.__dom=d;");
    gold.append("\n      var f=(function() {");
    gold.append("\n        $.ACLASS(this.__dom,((this['x']) ? (\" X is true \" + ((this['y']) ? (\" Y=true \") : (\" Y=false \"))) : (\" X is false \")));");
    gold.append("\n      }).bind(e);");
    gold.append("\n      $.Y(a,e,'x',f);");
    gold.append("\n      $.Y(a,e,'y',f);");
    gold.append("\n      f();");
    gold.append("\n    }");
    gold.append("\n    d.append($.T(' Well, something complex... '));");
    gold.append("\n    b.append(d);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed','nest'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    {");
    gold.append("\n      var e={};");
    gold.append("\n      e.__dom=d;");
    gold.append("\n      var f=(function() {");
    gold.append("\n        $.ACLASS(this.__dom,((this['x']) ? (\" X is true \" + ((this['y']) ? (\" Y=true \") : (\" Y=false \"))) : (\" X is false \")));");
    gold.append("\n      }).bind(e);");
    gold.append("\n      $.Y(a,e,'x',f);");
    gold.append("\n      $.Y(a,e,'y',f);");
    gold.append("\n      f();");
    gold.append("\n    }");
    gold.append("\n    d.append($.T(' Well, something complex... '));");
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
    source.append("\n    <page uri=\"/nest\">");
    source.append("\n        <div class=\"[x]X is true[y]Y=true[#]Y=false[/][#]X is false [/]\">");
    source.append("\n            Well, something complex...");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/nest\" : { }");
    gold.append("\n}");
    return gold.toString();
  }
}
