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

public class TemplateCustomConfigTests extends BaseRxHtmlTest {
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
    gold.append("\n    // <div rx:custom=\"c\" parameter:x=\"hi {xyz}\" port:y=\"my_error\" config:foo=\"donkey\">");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    var h=$.WX(['y',$.pV(a),'my_error']);");
    gold.append("\n");
    gold.append("\n    // <div rx:custom=\"c\" parameter:x=\"hi {xyz}\">");
    gold.append("\n    var i=$.RX(['x']);");
    gold.append("\n    $.Y2(a,i,'x','xyz',function(j) {");
    gold.append("\n      i.x=\"hi \" + $.F(j,'xyz')");
    gold.append("\n      i.__();");
    gold.append("\n    });");
    gold.append("\n    $.C(d,a,'c',i,h,function(f,e,g) {");
    gold.append("\n");
    gold.append("\n      // <span>");
    gold.append("\n      var j=$.E('span');");
    gold.append("\n      j.append($.T('A child element'));");
    gold.append("\n      f.append(j);");
    gold.append("\n    },{\"foo\":\"donkey\"});");
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
    gold.append("\n    // <div rx:custom=\"c\" parameter:x=\"hi {xyz}\" port:y=\"my_error\" config:foo=\"donkey\">");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    var h=$.WX(['y',$.pV(a),'my_error']);");
    gold.append("\n");
    gold.append("\n    // <div rx:custom=\"c\" parameter:x=\"hi {xyz}\">");
    gold.append("\n    var i=$.RX(['x']);");
    gold.append("\n    $.Y2(a,i,'x','xyz',function(j) {");
    gold.append("\n      i.x=\"hi \" + $.F(j,'xyz')");
    gold.append("\n      i.__();");
    gold.append("\n    });");
    gold.append("\n    $.C(d,a,'c',i,h,function(f,e,g) {");
    gold.append("\n");
    gold.append("\n      // <span>");
    gold.append("\n      var j=$.E('span');");
    gold.append("\n      j.append($.T('A child element'));");
    gold.append("\n      f.append(j);");
    gold.append("\n    },{\"foo\":\"donkey\"});");
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
    source.append("\n        <div rx:custom=\"c\" parameter:x=\"hi {xyz}\" port:y=\"my_error\" config:foo=\"donkey\">");
    source.append("\n            <span>A child element</span>");
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
