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

public class TemplateHighlightsTests extends BaseRxHtmlTest {
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
    gold.append("\n    // <code escape=\"\">");
    gold.append("\n    var d=$.E('code');");
    gold.append("\n    $.SA(d,'escape',true);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var e=$.E('b');");
    gold.append("\n    d.append(e);");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var d=$.E('b');");
    gold.append("\n    var e=$.E('pre');");
    gold.append("\n    d.append(e);");
    gold.append("\n    var f=$.E('code');");
    gold.append("\n    e.append(f);");
    gold.append("\n    $.ACLASS(f,'language-html');");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var g=$.E('b');");
    gold.append("\n    g.append($.T('not actually bold'));");
    gold.append("\n    f.append(g);");
    gold.append("\n    if (hljs) hljs.highlightElement(f);");
    gold.append("\n    var g=$.E('pre');");
    gold.append("\n    d.append(g);");
    gold.append("\n    var h=$.E('code');");
    gold.append("\n    g.append(h);");
    gold.append("\n    $.ACLASS(h,'language-adama');");
    gold.append("\n    h.append($.T(' public int x; '));");
    gold.append("\n    if (hljs) hljs.highlightElement(h);");
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
    gold.append("\n    // <code escape=\"\">");
    gold.append("\n    var d=$.E('code');");
    gold.append("\n    $.SA(d,'escape',true);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var e=$.E('b');");
    gold.append("\n    d.append(e);");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var d=$.E('b');");
    gold.append("\n    var e=$.E('pre');");
    gold.append("\n    d.append(e);");
    gold.append("\n    var f=$.E('code');");
    gold.append("\n    e.append(f);");
    gold.append("\n    $.ACLASS(f,'language-html');");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var g=$.E('b');");
    gold.append("\n    g.append($.T('not actually bold'));");
    gold.append("\n    f.append(g);");
    gold.append("\n    if (hljs) hljs.highlightElement(f);");
    gold.append("\n    var g=$.E('pre');");
    gold.append("\n    d.append(g);");
    gold.append("\n    var h=$.E('code');");
    gold.append("\n    g.append(h);");
    gold.append("\n    $.ACLASS(h,'language-adama');");
    gold.append("\n    h.append($.T(' public int x; '));");
    gold.append("\n    if (hljs) hljs.highlightElement(h);");
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
    source.append("\n        <code escape>");
    source.append("\n            <b>");
    source.append("\n        </code>");
    source.append("\n        <highlight lang=\"html\">");
    source.append("\n            <b>not actually bold</b>");
    source.append("\n        </highlight>");
    source.append("\n        <adama>");
    source.append("\n            public int x;");
    source.append("\n        </adama>");
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
