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

public class TemplateHrefTests extends BaseRxHtmlTest {
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
    gold.append("\n    // <a href=\"/here-i-am\">");
    gold.append("\n    var d=$.E('a');");
    gold.append("\n    $.HREF(d,a,\"/here-i-am\",false);");
    gold.append("\n    d.append($.T(' To rock you '));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <a href=\"/here-i-am/123\" merge=\"\">");
    gold.append("\n    var d=$.E('a');");
    gold.append("\n    $.HREF(d,a,\"/here-i-am/123\",true);");
    gold.append("\n    d.append($.T(' To rock you '));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <img src=\"/image.png\">");
    gold.append("\n    var d=$.E('img');");
    gold.append("\n    $.AS(d,\"/image.png\");");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <div class=\"someclass\">");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    $.AC(d,\" someclass \");");
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
    gold.append("\n    // <a href=\"/here-i-am\">");
    gold.append("\n    var d=$.E('a');");
    gold.append("\n    $.HREF(d,a,\"/here-i-am\",false);");
    gold.append("\n    d.append($.T(' To rock you '));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <a href=\"/here-i-am/123\" merge=\"\">");
    gold.append("\n    var d=$.E('a');");
    gold.append("\n    $.HREF(d,a,\"/here-i-am/123\",true);");
    gold.append("\n    d.append($.T(' To rock you '));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <img src=\"/image.png\">");
    gold.append("\n    var d=$.E('img');");
    gold.append("\n    $.AS(d,\"/image.png\");");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <div class=\"someclass\">");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    $.AC(d,\" someclass \");");
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
    source.append("\n        <a href=\"/here-i-am\">");
    source.append("\n            To rock you");
    source.append("\n        </a>");
    source.append("\n        <a href=\"/here-i-am/123\" merge>");
    source.append("\n            To rock you");
    source.append("\n        </a>");
    source.append("\n        <img src=\"/image.png\" />");
    source.append("\n        <div class=\"someclass\"></div>");
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
