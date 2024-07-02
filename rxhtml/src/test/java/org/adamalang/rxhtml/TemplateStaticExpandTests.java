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

public class TemplateStaticExpandTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:template 'navbar' was not used");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n");
    gold.append("\n  // <template name=\"navbar\">");
    gold.append("\n  $.TP('navbar', function(a,b,c,d) {");
    gold.append("\n    var e=$.X();");
    gold.append("\n");
    gold.append("\n    // <ul>");
    gold.append("\n    var f=$.E('ul');");
    gold.append("\n");
    gold.append("\n    // <a class=\"G2\" href=\"/page/1\">");
    gold.append("\n    var g=$.E('a');");
    gold.append("\n    $.ACLASS(g,\" G2 \");");
    gold.append("\n    $.HREF(g,b,\"/page/1\",false);");
    gold.append("\n    g.append($.T(' ['));");
    gold.append("\n    g.append($.T('Page 1'));");
    gold.append("\n    g.append($.T('] '));");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var g=$.E('br');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <div>");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <a class=\"G3\" href=\"/page/2\">");
    gold.append("\n    var g=$.E('a');");
    gold.append("\n    $.ACLASS(g,\" G3 \");");
    gold.append("\n    $.HREF(g,b,\"/page/2\",false);");
    gold.append("\n    g.append($.T(' ['));");
    gold.append("\n    g.append($.T('Page 2'));");
    gold.append("\n    g.append($.T('] '));");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var g=$.E('br');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <div>");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <a class=\"major\" href=\"/page/3\">");
    gold.append("\n    var g=$.E('a');");
    gold.append("\n    $.ACLASS(g,\" major \");");
    gold.append("\n    $.HREF(g,b,\"/page/3\",false);");
    gold.append("\n    g.append($.T(' ['));");
    gold.append("\n    g.append($.T('Page 3'));");
    gold.append("\n    g.append($.T('] '));");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var g=$.E('br');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <div attr_that=\"only_present_with_children\">");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    $.SA(g,'attr_that',\"only_present_with_children\");");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <div>");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n");
    gold.append("\n    // <ul>");
    gold.append("\n    var h=$.E('ul');");
    gold.append("\n    h.append($.T(' ++ '));");
    gold.append("\n");
    gold.append("\n    // <a class=\"G4\" href=\"/page/3/a\">");
    gold.append("\n    var i=$.E('a');");
    gold.append("\n    $.ACLASS(i,\" G4 \");");
    gold.append("\n    $.HREF(i,b,\"/page/3/a\",false);");
    gold.append("\n    i.append($.T(' ['));");
    gold.append("\n    i.append($.T('Part A'));");
    gold.append("\n    i.append($.T('] '));");
    gold.append("\n    h.append(i);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var i=$.E('br');");
    gold.append("\n    h.append(i);");
    gold.append("\n    g.append(h);");
    gold.append("\n    f.append(g);");
    gold.append("\n    a.append(f);");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/page/1\" static[nav]:label=\"Page 1\" static[nav]:code=\"%%%GENERATE%%%\" static[nav]:var=\"current\">");
    gold.append("\n  $.PG(['fixed','page','fixed','1'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/page/2\" static[nav]:label=\"Page 2\" static[nav]:code=\"%%%GENERATE%%%\" static[nav]:var=\"current\">");
    gold.append("\n  $.PG(['fixed','page','fixed','2'], function(b,a) {");
    gold.append("\n    var f=$.X();");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/page/3\" static[nav]:label=\"Page 3\" static[nav]:code=\"major\" static[nav]:var=\"current\">");
    gold.append("\n  $.PG(['fixed','page','fixed','3'], function(b,a) {");
    gold.append("\n    var g=$.X();");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/page/3/a\" static[nav]:label=\"Part A\" static[nav]:parent=\"major\" static[nav]:code=\"%%%GENERATE%%%\" static[nav]:var=\"current\">");
    gold.append("\n  $.PG(['fixed','page','fixed','3','fixed','a'], function(b,a) {");
    gold.append("\n    var h=$.X();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n");
    gold.append("\n  // <template name=\"navbar\">");
    gold.append("\n  $.TP('navbar', function(a,b,c,d) {");
    gold.append("\n    var e=$.X();");
    gold.append("\n");
    gold.append("\n    // <ul>");
    gold.append("\n    var f=$.E('ul');");
    gold.append("\n");
    gold.append("\n    // <a class=\"G2\" href=\"/page/1\">");
    gold.append("\n    var g=$.E('a');");
    gold.append("\n    $.ACLASS(g,\" G2 \");");
    gold.append("\n    $.HREF(g,b,\"/page/1\",false);");
    gold.append("\n    g.append($.T(' ['));");
    gold.append("\n    g.append($.T('Page 1'));");
    gold.append("\n    g.append($.T('] '));");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var g=$.E('br');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <div>");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <a class=\"G3\" href=\"/page/2\">");
    gold.append("\n    var g=$.E('a');");
    gold.append("\n    $.ACLASS(g,\" G3 \");");
    gold.append("\n    $.HREF(g,b,\"/page/2\",false);");
    gold.append("\n    g.append($.T(' ['));");
    gold.append("\n    g.append($.T('Page 2'));");
    gold.append("\n    g.append($.T('] '));");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var g=$.E('br');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <div>");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <a class=\"major\" href=\"/page/3\">");
    gold.append("\n    var g=$.E('a');");
    gold.append("\n    $.ACLASS(g,\" major \");");
    gold.append("\n    $.HREF(g,b,\"/page/3\",false);");
    gold.append("\n    g.append($.T(' ['));");
    gold.append("\n    g.append($.T('Page 3'));");
    gold.append("\n    g.append($.T('] '));");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var g=$.E('br');");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <div attr_that=\"only_present_with_children\">");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    $.SA(g,'attr_that',\"only_present_with_children\");");
    gold.append("\n    f.append(g);");
    gold.append("\n");
    gold.append("\n    // <div>");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n");
    gold.append("\n    // <ul>");
    gold.append("\n    var h=$.E('ul');");
    gold.append("\n    h.append($.T(' ++ '));");
    gold.append("\n");
    gold.append("\n    // <a class=\"G4\" href=\"/page/3/a\">");
    gold.append("\n    var i=$.E('a');");
    gold.append("\n    $.ACLASS(i,\" G4 \");");
    gold.append("\n    $.HREF(i,b,\"/page/3/a\",false);");
    gold.append("\n    i.append($.T(' ['));");
    gold.append("\n    i.append($.T('Part A'));");
    gold.append("\n    i.append($.T('] '));");
    gold.append("\n    h.append(i);");
    gold.append("\n");
    gold.append("\n    // <br>");
    gold.append("\n    var i=$.E('br');");
    gold.append("\n    h.append(i);");
    gold.append("\n    g.append(h);");
    gold.append("\n    f.append(g);");
    gold.append("\n    a.append(f);");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/page/1\" static[nav]:label=\"Page 1\" static[nav]:code=\"%%%GENERATE%%%\" static[nav]:var=\"current\">");
    gold.append("\n  $.PG(['fixed','page','fixed','1'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/page/2\" static[nav]:label=\"Page 2\" static[nav]:code=\"%%%GENERATE%%%\" static[nav]:var=\"current\">");
    gold.append("\n  $.PG(['fixed','page','fixed','2'], function(b,a) {");
    gold.append("\n    var f=$.X();");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/page/3\" static[nav]:label=\"Page 3\" static[nav]:code=\"major\" static[nav]:var=\"current\">");
    gold.append("\n  $.PG(['fixed','page','fixed','3'], function(b,a) {");
    gold.append("\n    var g=$.X();");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/page/3/a\" static[nav]:label=\"Part A\" static[nav]:parent=\"major\" static[nav]:code=\"%%%GENERATE%%%\" static[nav]:var=\"current\">");
    gold.append("\n  $.PG(['fixed','page','fixed','3','fixed','a'], function(b,a) {");
    gold.append("\n    var h=$.X();");
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
    source.append("\n    <template name=\"navbar\">");
    source.append("\n        <static-expand source=\"nav\">");
    source.append("\n            <ul static:iterate=\"pages\">");
    source.append("\n                <a class=\"%%%code%%%\" href=\"%%%nav:uri%%%\"> [<static:lookup name=\"label\" />] </a><br />");
    source.append("\n                <div static:guard:has_children:attr_that=\"only_present_with_children\"></div>");
    source.append("\n");
    source.append("\n                <div static:if=\"has_children\">");
    source.append("\n                    <ul static:iterate=\"children\">");
    source.append("\n                        ++ <a class=\"%%%code%%%\" href=\"%%%nav:uri%%%\"> [<static:lookup name=\"label\" />] </a><br />");
    source.append("\n                    </ul>");
    source.append("\n                </div>");
    source.append("\n            </ul>");
    source.append("\n        </static-expand>");
    source.append("\n    </template>");
    source.append("\n    <common-page uri:prefix=\"/page/\" static[nav]:var=\"current\" static[nav]:code=\"%%%GENERATE%%%\">");
    source.append("\n");
    source.append("\n    </common-page>");
    source.append("\n    <static-object name=\"foo\">");
    source.append("\n        {\"key\":\"value\"}");
    source.append("\n    </static-object>");
    source.append("\n    <page uri=\"/page/1\" static[nav]:label=\"Page 1\">");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/page/2\" static[nav]:label=\"Page 2\">");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/page/3\" static[nav]:label=\"Page 3\" static[nav]:code=\"major\">");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n");
    source.append("\n    <page uri=\"/page/3/a\" static[nav]:label=\"Part A\" static[nav]:parent=\"major\">");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/page/1\" : { },");
    gold.append("\n  \"/page/2\" : { },");
    gold.append("\n  \"/page/3\" : { },");
    gold.append("\n  \"/page/3/a\" : { }");
    gold.append("\n}");
    return gold.toString();
  }
}
