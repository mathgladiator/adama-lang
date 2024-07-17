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

public class TemplateStaticReplaceTests extends BaseRxHtmlTest {
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
    gold.append("\n  // <page uri=\"/3x\" static[nav]:label=\"Page 1\">");
    gold.append("\n  $.PG(['fixed','3x'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var d=$.E('br');");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var d=$.E('b');");
    gold.append("\n    d.append($.T('Hello World'));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var d=$.E('br');");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var d=$.E('b');");
    gold.append("\n    d.append($.T('Hello World'));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var d=$.E('br');");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var d=$.E('b');");
    gold.append("\n    d.append($.T('Hello World'));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var d=$.E('br');");
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
    gold.append("\n  // <page uri=\"/3x\" static[nav]:label=\"Page 1\">");
    gold.append("\n  $.PG(['fixed','3x'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var d=$.E('br');");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var d=$.E('b');");
    gold.append("\n    d.append($.T('Hello World'));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var d=$.E('br');");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var d=$.E('b');");
    gold.append("\n    d.append($.T('Hello World'));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var d=$.E('br');");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <b>");
    gold.append("\n    var d=$.E('b');");
    gold.append("\n    d.append($.T('Hello World'));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var d=$.E('br');");
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
    source.append("\n    <static-tree-fragment name=\"t\">");
    source.append("\n        <b>Hello World</b>");
    source.append("\n    </static-tree-fragment>");
    source.append("\n    <page uri=\"/3x\" static[nav]:label=\"Page 1\">");
    source.append("\n        <br />");
    source.append("\n        <static-tree-replace name=\"t\" />");
    source.append("\n        <br />");
    source.append("\n        <static-tree-replace name=\"t\" />");
    source.append("\n        <br />");
    source.append("\n        <static-tree-replace name=\"t\" />");
    source.append("\n        <br />");
    source.append("\n    </page>");
    source.append("\n");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/3x\" : { }");
    gold.append("\n}");
    return gold.toString();
  }
}