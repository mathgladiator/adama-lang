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

public class TemplateConfigTests extends BaseRxHtmlTest {
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
    gold.append("\n  // <template name=\"foo\">");
    gold.append("\n  $.TP('foo', function(a,b,c,d) {");
    gold.append("\n    var e=$.X();");
    gold.append("\n");
    gold.append("\n    // <div config:if=\"t\">");
    gold.append("\n    if($.CFGb(d,'t')) {");
    gold.append("\n      var f=$.E('div');");
    gold.append("\n      f.append($.T(' Show if T is true '));");
    gold.append("\n      a.append(f);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    // <div config:if=\"!f\">");
    gold.append("\n    if(!$.CFGb(d,'f')) {");
    gold.append("\n      var f=$.E('div');");
    gold.append("\n      f.append($.T(' Show if f is False '));");
    gold.append("\n      a.append(f);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    // <div config:if=\"i=123\">");
    gold.append("\n    if($.CFGt(d,'i',123)) {");
    gold.append("\n      var f=$.E('div');");
    gold.append("\n      f.append($.T(' Show if i is 123 '));");
    gold.append("\n      a.append(f);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    // <div config:if=\"!i=123\">");
    gold.append("\n    if(!$.CFGt(d,'i',123)) {");
    gold.append("\n      var f=$.E('div');");
    gold.append("\n      f.append($.T(' Show if i is not 123 '));");
    gold.append("\n      a.append(f);");
    gold.append("\n    }");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <div rx:template=\"foo\" config:t=\"true\" config:f=\"false\" config:i=\"123\" config:d=\"3.14\" config:s=\"hello world\">");
    gold.append("\n    var f=$.E('div');");
    gold.append("\n    $.UT(f,a,'foo', function(g,h,i) {");
    gold.append("\n    },{\"t\":true,\"f\":false,\"i\":123,\"d\":3.14,\"s\":\"hello world\"});");
    gold.append("\n    b.append(f);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n");
    gold.append("\n  // <template name=\"foo\">");
    gold.append("\n  $.TP('foo', function(a,b,c,d) {");
    gold.append("\n    var e=$.X();");
    gold.append("\n");
    gold.append("\n    // <div config:if=\"t\">");
    gold.append("\n    if($.CFGb(d,'t')) {");
    gold.append("\n      var f=$.E('div');");
    gold.append("\n      f.append($.T(' Show if T is true '));");
    gold.append("\n      a.append(f);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    // <div config:if=\"!f\">");
    gold.append("\n    if(!$.CFGb(d,'f')) {");
    gold.append("\n      var f=$.E('div');");
    gold.append("\n      f.append($.T(' Show if f is False '));");
    gold.append("\n      a.append(f);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    // <div config:if=\"i=123\">");
    gold.append("\n    if($.CFGt(d,'i',123)) {");
    gold.append("\n      var f=$.E('div');");
    gold.append("\n      f.append($.T(' Show if i is 123 '));");
    gold.append("\n      a.append(f);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    // <div config:if=\"!i=123\">");
    gold.append("\n    if(!$.CFGt(d,'i',123)) {");
    gold.append("\n      var f=$.E('div');");
    gold.append("\n      f.append($.T(' Show if i is not 123 '));");
    gold.append("\n      a.append(f);");
    gold.append("\n    }");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <div rx:template=\"foo\" config:t=\"true\" config:f=\"false\" config:i=\"123\" config:d=\"3.14\" config:s=\"hello world\">");
    gold.append("\n    var f=$.E('div');");
    gold.append("\n    $.UT(f,a,'foo', function(g,h,i) {");
    gold.append("\n    },{\"t\":true,\"f\":false,\"i\":123,\"d\":3.14,\"s\":\"hello world\"});");
    gold.append("\n    b.append(f);");
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
    source.append("\n    <template name=\"foo\">");
    source.append("\n        <div config:if=\"t\">");
    source.append("\n            Show if T is true");
    source.append("\n        </div>");
    source.append("\n        <div config:if=\"!f\">");
    source.append("\n            Show if f is False");
    source.append("\n        </div>");
    source.append("\n        <div config:if=\"i=123\">");
    source.append("\n            Show if i is 123");
    source.append("\n        </div>");
    source.append("\n        <div config:if=\"!i=123\">");
    source.append("\n            Show if i is not 123");
    source.append("\n        </div>");
    source.append("\n    </template>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <div rx:template=\"foo\" config:t=\"true\" config:f=\"false\" config:i=\"123\" config:d=\"3.14\" config:s=\"hello world\">");
    source.append("\n        </div>");
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
