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

public class TemplatePagifyTemplateEasyTests extends BaseRxHtmlTest {
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
    gold.append("\n    a.append($.T(' THIS IS A TEMPLATE '));");
    gold.append("\n    c(a,b,'');");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/a\" template:use=\"foo\">");
    gold.append("\n  $.PG(['fixed','a'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <div rx:template=\"foo\">");
    gold.append("\n    var f=$.E('div');");
    gold.append("\n    $.UT(f,a,'foo', function(g,h,i) {");
    gold.append("\n      g.append($.T(' BODY '));");
    gold.append("\n    },{});");
    gold.append("\n    b.append(f);");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/b\" template:use=\"foo\" template:tag=\"yikes\">");
    gold.append("\n  $.PG(['fixed','b'], function(b,a) {");
    gold.append("\n    var f=$.X();");
    gold.append("\n");
    gold.append("\n    // <yikes rx:template=\"foo\">");
    gold.append("\n    var g=$.E('yikes');");
    gold.append("\n    $.UT(g,a,'foo', function(h,i,j) {");
    gold.append("\n      h.append($.T(' BODY '));");
    gold.append("\n    },{});");
    gold.append("\n    b.append(g);");
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
    gold.append("\n    a.append($.T(' THIS IS A TEMPLATE '));");
    gold.append("\n    c(a,b,'');");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/a\" template:use=\"foo\">");
    gold.append("\n  $.PG(['fixed','a'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <div rx:template=\"foo\">");
    gold.append("\n    var f=$.E('div');");
    gold.append("\n    $.UT(f,a,'foo', function(g,h,i) {");
    gold.append("\n      g.append($.T(' BODY '));");
    gold.append("\n    },{});");
    gold.append("\n    b.append(f);");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/b\" template:use=\"foo\" template:tag=\"yikes\">");
    gold.append("\n  $.PG(['fixed','b'], function(b,a) {");
    gold.append("\n    var f=$.X();");
    gold.append("\n");
    gold.append("\n    // <yikes rx:template=\"foo\">");
    gold.append("\n    var g=$.E('yikes');");
    gold.append("\n    $.UT(g,a,'foo', function(h,i,j) {");
    gold.append("\n      h.append($.T(' BODY '));");
    gold.append("\n    },{});");
    gold.append("\n    b.append(g);");
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
    source.append("\n        THIS IS A TEMPLATE <fragment />");
    source.append("\n    </template>");
    source.append("\n    <page uri=\"/a\" template:use=\"foo\">");
    source.append("\n        BODY");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/b\" template:use=\"foo\" template:tag=\"yikes\">");
    source.append("\n        BODY");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/a\" : { },");
    gold.append("\n  \"/b\" : { }");
    gold.append("\n}");
    return gold.toString();
  }
}
