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

public class TemplateTemplateInlineTests extends BaseRxHtmlTest {
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
    gold.append("\n  // <template name=\"temp\" ln:ch=\"0;0;0;22;templateInline.rx.html\">");
    gold.append("\n  $.TP('temp', function(a,b,c) {");
    gold.append("\n    var d=$.X();");
    gold.append("\n    a.append($.T(' This is a template with a fragment: '));");
    gold.append("\n    c(a,b,'foo');");
    gold.append("\n");
    gold.append("\n    // <hr ln:ch=\"2;1;2;5;templateInline.rx.html\">");
    gold.append("\n    var e=$.E('hr');");
    gold.append("\n    a.append(e);");
    gold.append("\n    c(a,b,'boo');");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\" ln:ch=\"4;0;4;14;templateInline.rx.html\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    b.append($.T(' This is a page which is going to use the template. '));");
    gold.append("\n    $.UT(b,a,'', function(e,f,g) {");
    gold.append("\n      if (g == 'foo') {");
    gold.append("\n");
    gold.append("\n        // <div rx:case=\"foo\" ln:ch=\"7;2;7;21;templateInline.rx.html\">");
    gold.append("\n        var h=$.E('div');");
    gold.append("\n        h.append($.T(' Foo '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n      if (g == 'boo') {");
    gold.append("\n");
    gold.append("\n        // <div rx:case=\"boo\" ln:ch=\"10;2;10;21;templateInline.rx.html\">");
    gold.append("\n        var h=$.E('div');");
    gold.append("\n        h.append($.T(' Boo '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n    });");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n");
    gold.append("\n  // <template name=\"temp\" ln:ch=\"0;0;0;22;templateInline.rx.html\">");
    gold.append("\n  $.TP('temp', function(a,b,c) {");
    gold.append("\n    var d=$.X();");
    gold.append("\n    a.append($.T(' This is a template with a fragment: '));");
    gold.append("\n    c(a,b,'foo');");
    gold.append("\n");
    gold.append("\n    // <hr ln:ch=\"2;1;2;5;templateInline.rx.html\">");
    gold.append("\n    var e=$.E('hr');");
    gold.append("\n    a.append(e);");
    gold.append("\n    c(a,b,'boo');");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\" ln:ch=\"4;0;4;14;templateInline.rx.html\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    b.append($.T(' This is a page which is going to use the template. '));");
    gold.append("\n    $.UT(b,a,'', function(e,f,g) {");
    gold.append("\n      if (g == 'foo') {");
    gold.append("\n");
    gold.append("\n        // <div rx:case=\"foo\" ln:ch=\"7;2;7;21;templateInline.rx.html\">");
    gold.append("\n        var h=$.E('div');");
    gold.append("\n        h.append($.T(' Foo '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n      if (g == 'boo') {");
    gold.append("\n");
    gold.append("\n        // <div rx:case=\"boo\" ln:ch=\"10;2;10;21;templateInline.rx.html\">");
    gold.append("\n        var h=$.E('div');");
    gold.append("\n        h.append($.T(' Boo '));");
    gold.append("\n        e.append(h);");
    gold.append("\n      }");
    gold.append("\n    });");
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
    source.append("\n    <template name=\"temp\">");
    source.append("\n        This is a template with a fragment: <fragment case=\"foo\" />");
    source.append("\n        <hr />");
    source.append("\n        <fragment case=\"boo\" />");
    source.append("\n    </template>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        This is a page which is going to use the template.");
    source.append("\n        <inline-template template=\"temp\">");
    source.append("\n            <div rx:case=\"foo\"> Foo </div>");
    source.append("\n            <div rx:case=\"boo\"> Boo </div>");
    source.append("\n        </inline-template>");
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
