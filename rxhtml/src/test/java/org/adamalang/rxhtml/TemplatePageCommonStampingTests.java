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

public class TemplatePageCommonStampingTests extends BaseRxHtmlTest {
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
    gold.append("\n  // <template name=\"t\">");
    gold.append("\n  $.TP('t', function(a,b,c) {");
    gold.append("\n    var d=$.X();");
    gold.append("\n    a.append($.T(' XYZ '));");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/foo\" init:show=\"keep-me\" template:tag=\"dog\" init:foo=\"foo\" template:use=\"t\">");
    gold.append("\n  $.PG(['fixed','foo'], function(b,a) {");
    gold.append("\n    a.view.init['show']='keep-me';");
    gold.append("\n    a.view.init['foo']='foo';");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <dog rx:template=\"t\">");
    gold.append("\n    var e=$.E('dog');");
    gold.append("\n    $.UT(e,a,'t', function(f,g,h) {");
    gold.append("\n      f.append($.T(' Yo '));");
    gold.append("\n    });");
    gold.append("\n    b.append(e);");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/foody\" template:tag=\"dog\" init:foo=\"foo\" init:show=\"showing\" template:use=\"t\">");
    gold.append("\n  $.PG(['fixed','foody'], function(b,a) {");
    gold.append("\n    a.view.init['foo']='foo';");
    gold.append("\n    a.view.init['show']='showing';");
    gold.append("\n    var e=$.X();");
    gold.append("\n");
    gold.append("\n    // <dog rx:template=\"t\">");
    gold.append("\n    var f=$.E('dog');");
    gold.append("\n    $.UT(f,a,'t', function(g,h,i) {");
    gold.append("\n      g.append($.T(' Food? '));");
    gold.append("\n    });");
    gold.append("\n    b.append(f);");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/g\">");
    gold.append("\n  $.PG(['fixed','g'], function(b,a) {");
    gold.append("\n    var f=$.X();");
    gold.append("\n    b.append($.T(' Yo '));");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n");
    gold.append("\n  // <template name=\"t\">");
    gold.append("\n  $.TP('t', function(a,b,c) {");
    gold.append("\n    var d=$.X();");
    gold.append("\n    a.append($.T(' XYZ '));");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/foo\" init:show=\"keep-me\" template:tag=\"dog\" init:foo=\"foo\" template:use=\"t\">");
    gold.append("\n  $.PG(['fixed','foo'], function(b,a) {");
    gold.append("\n    a.view.init['show']='keep-me';");
    gold.append("\n    a.view.init['foo']='foo';");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <dog rx:template=\"t\">");
    gold.append("\n    var e=$.E('dog');");
    gold.append("\n    $.UT(e,a,'t', function(f,g,h) {");
    gold.append("\n      f.append($.T(' Yo '));");
    gold.append("\n    });");
    gold.append("\n    b.append(e);");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/foody\" template:tag=\"dog\" init:foo=\"foo\" init:show=\"showing\" template:use=\"t\">");
    gold.append("\n  $.PG(['fixed','foody'], function(b,a) {");
    gold.append("\n    a.view.init['foo']='foo';");
    gold.append("\n    a.view.init['show']='showing';");
    gold.append("\n    var e=$.X();");
    gold.append("\n");
    gold.append("\n    // <dog rx:template=\"t\">");
    gold.append("\n    var f=$.E('dog');");
    gold.append("\n    $.UT(f,a,'t', function(g,h,i) {");
    gold.append("\n      g.append($.T(' Food? '));");
    gold.append("\n    });");
    gold.append("\n    b.append(f);");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/g\">");
    gold.append("\n  $.PG(['fixed','g'], function(b,a) {");
    gold.append("\n    var f=$.X();");
    gold.append("\n    b.append($.T(' Yo '));");
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
    source.append("\n    <template name=\"t\">");
    source.append("\n        XYZ");
    source.append("\n    </template>");
    source.append("\n    <common-page uri:prefix=\"/f\" init:foo=\"foo\" init:show=\"showing\" template:use=\"t\" template:tag=\"dog\" />");
    source.append("\n    <page uri=\"/foo\" init:show=\"keep-me\">");
    source.append("\n        Yo");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/foody\">");
    source.append("\n        Food?");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/g\">");
    source.append("\n        Yo");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/foo\" : { },");
    gold.append("\n  \"/foody\" : { },");
    gold.append("\n  \"/g\" : { }");
    gold.append("\n}");
    return gold.toString();
  }
}
