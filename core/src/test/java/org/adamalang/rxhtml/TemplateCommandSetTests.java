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

public class TemplateCommandSetTests extends BaseRxHtmlTest {
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
    gold.append("\n  // <page uri=\"/\" ln:ch=\"0;0;0;14;commandSet.rx.html\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <button rx:click=\"set:x={view:here}\" ln:ch=\"1;1;1;38;commandSet.rx.html\">");
    gold.append("\n    var d=$.E('button');");
    gold.append("\n    var e=[];");
    gold.append("\n    var f={};");
    gold.append("\n    $.YS($.pV(a),f,'here');");
    gold.append("\n    e.push($.bS(d,$.pV(a),'x',function(){ return f['here'];}));");
    gold.append("\n    $.onB(d,'click',a,e);");
    gold.append("\n    d.append($.T('Set View'));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <button rx:click=\"set:x={data:there} set:y=\\'The Big Long Title\\'\" ln:ch=\"2;1;2;66;commandSet.rx.html\">");
    gold.append("\n    var d=$.E('button');");
    gold.append("\n    var g=[];");
    gold.append("\n    var h={};");
    gold.append("\n    $.YS($.pD(a),h,'there');");
    gold.append("\n    g.push($.bS(d,$.pV(a),'x',function(){ return h['there'];}));");
    gold.append("\n    g.push($.bS(d,$.pV(a),'y','The Big Long Title'));");
    gold.append("\n    $.onB(d,'click',a,g);");
    gold.append("\n    d.append($.T('Set Data'));");
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
    gold.append("\n  // <page uri=\"/\" ln:ch=\"0;0;0;14;commandSet.rx.html\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <button rx:click=\"set:x={view:here}\" ln:ch=\"1;1;1;38;commandSet.rx.html\">");
    gold.append("\n    var d=$.E('button');");
    gold.append("\n    var e=[];");
    gold.append("\n    var f={};");
    gold.append("\n    $.YS($.pV(a),f,'here');");
    gold.append("\n    e.push($.bS(d,$.pV(a),'x',function(){ return f['here'];}));");
    gold.append("\n    $.onB(d,'click',a,e);");
    gold.append("\n    d.append($.T('Set View'));");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <button rx:click=\"set:x={data:there} set:y=\\'The Big Long Title\\'\" ln:ch=\"2;1;2;66;commandSet.rx.html\">");
    gold.append("\n    var d=$.E('button');");
    gold.append("\n    var g=[];");
    gold.append("\n    var h={};");
    gold.append("\n    $.YS($.pD(a),h,'there');");
    gold.append("\n    g.push($.bS(d,$.pV(a),'x',function(){ return h['there'];}));");
    gold.append("\n    g.push($.bS(d,$.pV(a),'y','The Big Long Title'));");
    gold.append("\n    $.onB(d,'click',a,g);");
    gold.append("\n    d.append($.T('Set Data'));");
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
    source.append("\n        <button rx:click=\"set:x={view:here}\">Set View</button>");
    source.append("\n        <button rx:click=\"set:x={data:there} set:y='The Big Long Title'\">Set Data</button>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/\" : {");
    gold.append("\n    \"here\" : \"value\",");
    gold.append("\n    \"x\" : \"string-formula\",");
    gold.append("\n    \"y\" : \"string\"");
    gold.append("\n  }");
    gold.append("\n}");
    return gold.toString();
  }
}
