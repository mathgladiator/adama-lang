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

public class TemplateMonitorSimpleTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:no data channel for a form to send channel 'blah' on");
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
    gold.append("\n    // <form rx:action=\"send:blah\" id=\"my-form-of-awesome\">");
    gold.append("\n    var d=$.E('form');");
    gold.append("\n    $.SA(d,'id',\"my-form-of-awesome\");");
    gold.append("\n    $.aSD(d,a,'blah',0);");
    gold.append("\n    var e=[];");
    gold.append("\n    e.push($.bS(d,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(d,'success',a,e);");
    gold.append("\n    var f=[];");
    gold.append("\n    f.push($.bS(d,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(d,'failure',a,f);");
    gold.append("\n");
    gold.append("\n    // <div rx:monitor=\"set\" rx:fall=\"set:x=0\" rx:rise=\"submit\">");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    var h=[];");
    gold.append("\n    h.push($.bSB(g));");
    gold.append("\n    $.onB(g,'rise',a,h);");
    gold.append("\n    var i=[];");
    gold.append("\n    i.push($.bS(g,$.pV(a),'x',0));");
    gold.append("\n    $.onB(g,'fall',a,i);");
    gold.append("\n    $.MN(g,a,'set');");
    gold.append("\n    d.append(g);");
    gold.append("\n    $.MN(d,a,'set-e',false,10);");
    gold.append("\n    var g=[];");
    gold.append("\n    g.push($.bSB(d));");
    gold.append("\n    $.onB(d,'rise',a,g);");
    gold.append("\n    var j=[];");
    gold.append("\n    j.push($.bS(d,$.pV(a),'x',0));");
    gold.append("\n    $.onB(d,'fall',a,j);");
    gold.append("\n    $.MN(d,a,'set-skip',true,10);");
    gold.append("\n    var k=[];");
    gold.append("\n    k.push($.bSB(d));");
    gold.append("\n    $.onB(d,'rise',a,k);");
    gold.append("\n    var l=[];");
    gold.append("\n    l.push($.bS(d,$.pV(a),'x',0));");
    gold.append("\n    $.onB(d,'fall',a,l);");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <div rx:monitor=\"set\" rx:fall=\"set:x=0\" rx:rise=\"submit:my-form-of-awesome\">");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    $.oSFI(d,'rise',a,'my-form-of-awesome');");
    gold.append("\n    var m=[];");
    gold.append("\n    m.push($.bS(d,$.pV(a),'x',0));");
    gold.append("\n    $.onB(d,'fall',a,m);");
    gold.append("\n    $.MN(d,a,'set');");
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
    gold.append("\n    // <form rx:action=\"send:blah\" id=\"my-form-of-awesome\">");
    gold.append("\n    var d=$.E('form');");
    gold.append("\n    $.SA(d,'id',\"my-form-of-awesome\");");
    gold.append("\n    $.aSD(d,a,'blah',0);");
    gold.append("\n    var e=[];");
    gold.append("\n    e.push($.bS(d,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(d,'success',a,e);");
    gold.append("\n    var f=[];");
    gold.append("\n    f.push($.bS(d,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(d,'failure',a,f);");
    gold.append("\n");
    gold.append("\n    // <div rx:monitor=\"set\" rx:fall=\"set:x=0\" rx:rise=\"submit\">");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    var h=[];");
    gold.append("\n    h.push($.bSB(g));");
    gold.append("\n    $.onB(g,'rise',a,h);");
    gold.append("\n    var i=[];");
    gold.append("\n    i.push($.bS(g,$.pV(a),'x',0));");
    gold.append("\n    $.onB(g,'fall',a,i);");
    gold.append("\n    $.MN(g,a,'set');");
    gold.append("\n    d.append(g);");
    gold.append("\n    $.MN(d,a,'set-e',false,10);");
    gold.append("\n    var g=[];");
    gold.append("\n    g.push($.bSB(d));");
    gold.append("\n    $.onB(d,'rise',a,g);");
    gold.append("\n    var j=[];");
    gold.append("\n    j.push($.bS(d,$.pV(a),'x',0));");
    gold.append("\n    $.onB(d,'fall',a,j);");
    gold.append("\n    $.MN(d,a,'set-skip',true,10);");
    gold.append("\n    var k=[];");
    gold.append("\n    k.push($.bSB(d));");
    gold.append("\n    $.onB(d,'rise',a,k);");
    gold.append("\n    var l=[];");
    gold.append("\n    l.push($.bS(d,$.pV(a),'x',0));");
    gold.append("\n    $.onB(d,'fall',a,l);");
    gold.append("\n    b.append(d);");
    gold.append("\n");
    gold.append("\n    // <div rx:monitor=\"set\" rx:fall=\"set:x=0\" rx:rise=\"submit:my-form-of-awesome\">");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    $.oSFI(d,'rise',a,'my-form-of-awesome');");
    gold.append("\n    var m=[];");
    gold.append("\n    m.push($.bS(d,$.pV(a),'x',0));");
    gold.append("\n    $.onB(d,'fall',a,m);");
    gold.append("\n    $.MN(d,a,'set');");
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
    source.append("\n        <form rx:action=\"send:blah\" id=\"my-form-of-awesome\">");
    source.append("\n            <div rx:monitor=\"set\" rx:fall=\"set:x=0\" rx:rise=\"submit\">");
    source.append("\n            </div>");
    source.append("\n            <monitor path=\"set-e\" rx:fall=\"set:x=0\" rx:rise=\"submit\" />");
    source.append("\n            <monitor path=\"set-skip\" rx:fall=\"set:x=0\" rx:rise=\"submit\" skip-first />");
    source.append("\n        </form>");
    source.append("\n");
    source.append("\n        <div rx:monitor=\"set\" rx:fall=\"set:x=0\" rx:rise=\"submit:my-form-of-awesome\">");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/\" : {");
    gold.append("\n    \"x\" : \"int\"");
    gold.append("\n  }");
    gold.append("\n}");
    return gold.toString();
  }
}
