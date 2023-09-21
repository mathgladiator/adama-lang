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

public class TemplateBadDelayTimeTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:/:delay 'delayx' is not a well formed delay");
    issues.append("\nWARNING:/:delay 'delay:x' is not a well formed delay (failed to parse x as int)");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'foo');");
    gold.append("\n    var d=[];");
    gold.append("\n    d.push($.bS(c,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(c,'success',a,d);");
    gold.append("\n    var e=[];");
    gold.append("\n    e.push($.bS(c,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,e);");
    gold.append("\n    var f = $.E('div');");
    gold.append("\n    $.oSBMT(f,'delayx',a);");
    gold.append("\n    c.append(f);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'foo');");
    gold.append("\n    var f=[];");
    gold.append("\n    f.push($.bS(c,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(c,'success',a,f);");
    gold.append("\n    var h=[];");
    gold.append("\n    h.push($.bS(c,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,h);");
    gold.append("\n    var i = $.E('div');");
    gold.append("\n    $.oSBMT(i,'delay:x',a);");
    gold.append("\n    c.append(i);");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'foo');");
    gold.append("\n    var d=[];");
    gold.append("\n    d.push($.bS(c,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(c,'success',a,d);");
    gold.append("\n    var e=[];");
    gold.append("\n    e.push($.bS(c,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,e);");
    gold.append("\n    var f = $.E('div');");
    gold.append("\n    $.oSBMT(f,'delayx',a);");
    gold.append("\n    c.append(f);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'foo');");
    gold.append("\n    var f=[];");
    gold.append("\n    f.push($.bS(c,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(c,'success',a,f);");
    gold.append("\n    var h=[];");
    gold.append("\n    h.push($.bS(c,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,h);");
    gold.append("\n    var i = $.E('div');");
    gold.append("\n    $.oSBMT(i,'delay:x',a);");
    gold.append("\n    c.append(i);");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</script><style>");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</style></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <form rx:action=\"send:foo\">");
    source.append("\n            <div rx:delayX=\"submit\"></div>");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"send:foo\">");
    source.append("\n            <div rx:delay:X=\"submit\"></div>");
    source.append("\n        </form>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
