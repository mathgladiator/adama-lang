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

public class TemplateChoosenSimpleTests extends BaseRxHtmlTest {
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
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='default';");
    gold.append("\n    d.space='space';");
    gold.append("\n    d.key='key';");
    gold.append("\n    d.identity=true;");
    gold.append("\n    d.redirect='/sign-in';");
    gold.append("\n    $.CONNECT(a,d);");
    gold.append("\n    d.__();");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='default';");
    gold.append("\n    $.P(b,a,e,function(b,f) {");
    gold.append("\n      var g=$.E('div');");
    gold.append("\n      $.DE(g,f,f,'channel','id','id',true,false,function(i,h) {");
    gold.append("\n        var j=$.E('button');");
    gold.append("\n        $.exCH(j,'click',h,'id','channel','id');");
    gold.append("\n        var k=$.E('span');");
    gold.append("\n        $.CSEN(k,h,h,'channel','id','id',true,false,function(m,l) {");
    gold.append("\n          m.append($.T(' Add Choice '));");
    gold.append("\n        },function(m,l) {");
    gold.append("\n          var n=$.E('span');");
    gold.append("\n          n.append($.T('Remove Choice'));");
    gold.append("\n          m.append(n);");
    gold.append("\n        },false);");
    gold.append("\n        j.append(k);");
    gold.append("\n        i.append(j);");
    gold.append("\n      },function(i,h) {");
    gold.append("\n      },false);");
    gold.append("\n      b.append(g);");
    gold.append("\n      var g=$.E('div');");
    gold.append("\n      $.FIN(g,f,'channel',true,false,function(i,h) {");
    gold.append("\n        var j=$.E('button');");
    gold.append("\n        $.exFIN(j,'click',h,'channel');");
    gold.append("\n        j.append($.T('Send choices'));");
    gold.append("\n        i.append(j);");
    gold.append("\n        i.append($.T(' Time to decide! '));");
    gold.append("\n      },function(i,h) {");
    gold.append("\n      },false);");
    gold.append("\n      b.append(g);");
    gold.append("\n    },function(b,f) {");
    gold.append("\n    });");
    gold.append("\n    e.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='default';");
    gold.append("\n    d.space='space';");
    gold.append("\n    d.key='key';");
    gold.append("\n    d.identity=true;");
    gold.append("\n    d.redirect='/sign-in';");
    gold.append("\n    $.CONNECT(a,d);");
    gold.append("\n    d.__();");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='default';");
    gold.append("\n    $.P(b,a,e,function(b,f) {");
    gold.append("\n      var g=$.E('div');");
    gold.append("\n      $.DE(g,f,f,'channel','id','id',true,false,function(i,h) {");
    gold.append("\n        var j=$.E('button');");
    gold.append("\n        $.exCH(j,'click',h,'id','channel','id');");
    gold.append("\n        var k=$.E('span');");
    gold.append("\n        $.CSEN(k,h,h,'channel','id','id',true,false,function(m,l) {");
    gold.append("\n          m.append($.T(' Add Choice '));");
    gold.append("\n        },function(m,l) {");
    gold.append("\n          var n=$.E('span');");
    gold.append("\n          n.append($.T('Remove Choice'));");
    gold.append("\n          m.append(n);");
    gold.append("\n        },false);");
    gold.append("\n        j.append(k);");
    gold.append("\n        i.append(j);");
    gold.append("\n      },function(i,h) {");
    gold.append("\n      },false);");
    gold.append("\n      b.append(g);");
    gold.append("\n      var g=$.E('div');");
    gold.append("\n      $.FIN(g,f,'channel',true,false,function(i,h) {");
    gold.append("\n        var j=$.E('button');");
    gold.append("\n        $.exFIN(j,'click',h,'channel');");
    gold.append("\n        j.append($.T('Send choices'));");
    gold.append("\n        i.append(j);");
    gold.append("\n        i.append($.T(' Time to decide! '));");
    gold.append("\n      },function(i,h) {");
    gold.append("\n      },false);");
    gold.append("\n      b.append(g);");
    gold.append("\n    },function(b,f) {");
    gold.append("\n    });");
    gold.append("\n    e.__();");
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
    source.append("\n<page uri=\"/\">");
    source.append("\n        <connection space=\"space\" key=\"key\">");
    source.append("\n            <div rx:if=\"choose:channel\">");
    source.append("\n                <button rx:click=\"choose:channel\">");
    source.append("\n                    <span rx:if=\"chosen:channel\">");
    source.append("\n                        Add Choice");
    source.append("\n                        <span rx:else>Remove Choice</span>");
    source.append("\n                    </span>");
    source.append("\n                </button>");
    source.append("\n            </div>");
    source.append("\n            <div rx:if=\"finalize:channel\">");
    source.append("\n                <button rx:click=\"finalize:channel\">Send choices</button>");
    source.append("\n                Time to decide!");
    source.append("\n            </div>");
    source.append("\n        </connection>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/\" : {");
    gold.append("\n    \"choose:channel\" : \"bool\",");
    gold.append("\n    \"chosen:channel\" : \"bool\",");
    gold.append("\n    \"finalize:channel\" : \"bool\"");
    gold.append("\n  }");
    gold.append("\n}");
    return gold.toString();
  }
}
