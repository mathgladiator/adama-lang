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

public class TemplateReactiveConnectionTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c=$.RX(['space','key']);");
    gold.append("\n    c.name='myname';");
    gold.append("\n    $.Y2(a,c,'space','suffix',function(d) {");
    gold.append("\n      c.space=\"space\" + d['suffix']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2($.pR($.pV(a)),c,'key','key',function(d) {");
    gold.append("\n      c.key=d['key']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    c.identity=true;");
    gold.append("\n    c.redirect='/sign-in';");
    gold.append("\n    $.CONNECT(a,c);");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    var f=$.E('div');");
    gold.append("\n    b.append(f);");
    gold.append("\n    $.P(f,a,d,function(f,e) {");
    gold.append("\n    },function(f,e) {");
    gold.append("\n    });");
    gold.append("\n    d.__();");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='myname';");
    gold.append("\n    var h=$.E('div');");
    gold.append("\n    b.append(h);");
    gold.append("\n    $.P(h,a,e,function(h,g) {");
    gold.append("\n    },function(h,g) {");
    gold.append("\n    });");
    gold.append("\n    e.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.RX(['space','key']);");
    gold.append("\n    c.name='myname';");
    gold.append("\n    $.Y2(a,c,'space','suffix',function(d) {");
    gold.append("\n      c.space=\"space\" + d['suffix']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2($.pR($.pV(a)),c,'key','key',function(d) {");
    gold.append("\n      c.key=d['key']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    c.identity=true;");
    gold.append("\n    c.redirect='/sign-in';");
    gold.append("\n    $.CONNECT(a,c);");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    var f=$.E('div');");
    gold.append("\n    b.append(f);");
    gold.append("\n    $.P(f,a,d,function(f,e) {");
    gold.append("\n    },function(f,e) {");
    gold.append("\n    });");
    gold.append("\n    d.__();");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='myname';");
    gold.append("\n    var h=$.E('div');");
    gold.append("\n    b.append(h);");
    gold.append("\n    $.P(h,a,e,function(h,g) {");
    gold.append("\n    },function(h,g) {");
    gold.append("\n    });");
    gold.append("\n    e.__();");
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
    source.append("\n<page uri=\"/\">");
    source.append("\n        <connection name=\"myname\" space=\"space{suffix}\" key=\"{view:/key}\">");
    source.append("\n");
    source.append("\n        </connection>");
    source.append("\n        <pick name=\"myname\">");
    source.append("\n");
    source.append("\n        </pick>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
