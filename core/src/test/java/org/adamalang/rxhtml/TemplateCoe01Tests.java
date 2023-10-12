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

public class TemplateCoe01Tests extends BaseRxHtmlTest {
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
    gold.append("\n  $.PG(['fixed','huh'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    var d=$.E('fieldset');");
    gold.append("\n    $.SA(d,'name',\"color\");");
    gold.append("\n    {");
    gold.append("\n      var e={};");
    gold.append("\n      e.__dom=d;");
    gold.append("\n      var f=(function() {");
    gold.append("\n        $.ACLASS(this.__dom,\" blad \" + (((this['c']==\"'\" + this['id'] + \"'\")) ? (\" block \") : (\" hidden \")));");
    gold.append("\n      }).bind(e);");
    gold.append("\n      $.Y($.pV(a),e,'c',f);");
    gold.append("\n      $.Y(a,e,'id',f);");
    gold.append("\n      f();");
    gold.append("\n    }");
    gold.append("\n    b.append(d);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed','huh'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    var d=$.E('fieldset');");
    gold.append("\n    $.SA(d,'name',\"color\");");
    gold.append("\n    {");
    gold.append("\n      var e={};");
    gold.append("\n      e.__dom=d;");
    gold.append("\n      var f=(function() {");
    gold.append("\n        $.ACLASS(this.__dom,\" blad \" + (((this['c']==\"'\" + this['id'] + \"'\")) ? (\" block \") : (\" hidden \")));");
    gold.append("\n      }).bind(e);");
    gold.append("\n      $.Y($.pV(a),e,'c',f);");
    gold.append("\n      $.Y(a,e,'id',f);");
    gold.append("\n      f();");
    gold.append("\n    }");
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
    source.append("\n    <page uri=\"/huh\">");
    source.append("\n        <fieldset");
    source.append("\n                name=\"color\"");
    source.append("\n                class=\"blad [view:c='{id}'] block [#view:c='{id}'] hidden [/view:c='{id}']\"");
    source.append("\n        >");
    source.append("\n        </fieldset>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/huh\" : {");
    gold.append("\n    \"c\" : \"value\"");
    gold.append("\n  }");
    gold.append("\n}");
    return gold.toString();
  }
}
