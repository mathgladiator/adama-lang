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

public class TemplateSchemaTests extends BaseRxHtmlTest {
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
    gold.append("\n  $.TP('foo', function(a,b,c) {");
    gold.append("\n    var d=$.X();");
    gold.append("\n    var e=$.E('button');");
    gold.append("\n    var f=[];");
    gold.append("\n    f.push($.bS(e,$.pV(b),'y',3));");
    gold.append("\n    $.onB(e,'click',b,f);");
    gold.append("\n    e.append($.T('Set Y'));");
    gold.append("\n    a.append(e);");
    gold.append("\n  });");
    gold.append("\n  $.TP('foo_scope', function(a,b,c) {");
    gold.append("\n    var e=$.X();");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    var h=$.pI(b,'fooscope');");
    gold.append("\n    c(g,h,'');");
    gold.append("\n    a.append(g);");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','should_be_canonical_test_for_schema_gen'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    var i=$.pIE(a,'child', true);");
    gold.append("\n    var j=$.E('button');");
    gold.append("\n    var k=[];");
    gold.append("\n    k.push($.bS(j,$.pV(i),'x',1));");
    gold.append("\n    $.onB(j,'click',i,k);");
    gold.append("\n    j.append($.T('Button In Child'));");
    gold.append("\n    g.append(j);");
    gold.append("\n    var j=$.E('div');");
    gold.append("\n    $.UT(j,i,'foo', function(l,m,n) {");
    gold.append("\n      l.append($.T(' Fragment 1 '));");
    gold.append("\n    });");
    gold.append("\n    g.append(j);");
    gold.append("\n    b.append(g);");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    $.IT(g,a,'children',true,function(j) {");
    gold.append("\n      var l=$.E('div');");
    gold.append("\n      var m=$.E('button');");
    gold.append("\n      var n=[];");
    gold.append("\n      n.push($.bS(m,$.pV(j),'z','x'));");
    gold.append("\n      $.onB(m,'click',j,n);");
    gold.append("\n      m.append($.T('Button In Children'));");
    gold.append("\n      l.append(m);");
    gold.append("\n      var m=$.E('div');");
    gold.append("\n      $.UT(m,j,'foo', function(o,p,q) {");
    gold.append("\n        o.append($.T(' Fragment 2 '));");
    gold.append("\n      });");
    gold.append("\n      l.append(m);");
    gold.append("\n      return l;");
    gold.append("\n    });");
    gold.append("\n    b.append(g);");
    gold.append("\n    b.append($.L(a,'should_no_be_in_view'));");
    gold.append("\n    b.append($.L($.pV(a),'w'));");
    gold.append("\n    var g=$.E('button');");
    gold.append("\n    var j=[];");
    gold.append("\n    j.push($.bS(g,$.pV(a),'w',1));");
    gold.append("\n    $.onB(g,'click',a,j);");
    gold.append("\n    g.append($.T('Button Root'));");
    gold.append("\n    b.append(g);");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    $.UT(g,a,'foo', function(l,m,o) {");
    gold.append("\n      l.append($.T(' Fragment 3 '));");
    gold.append("\n    });");
    gold.append("\n    b.append(g);");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    $.UT(g,a,'foo_scope', function(l,m,o) {");
    gold.append("\n      var p=$.E('button');");
    gold.append("\n      var q=[];");
    gold.append("\n      q.push($.bS(p,$.pV(m),'v',1));");
    gold.append("\n      $.onB(p,'click',m,q);");
    gold.append("\n      p.append($.T('Fragment Button'));");
    gold.append("\n      l.append(p);");
    gold.append("\n    });");
    gold.append("\n    b.append(g);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.TP('foo', function(a,b,c) {");
    gold.append("\n    var d=$.X();");
    gold.append("\n    var e=$.E('button');");
    gold.append("\n    var f=[];");
    gold.append("\n    f.push($.bS(e,$.pV(b),'y',3));");
    gold.append("\n    $.onB(e,'click',b,f);");
    gold.append("\n    e.append($.T('Set Y'));");
    gold.append("\n    a.append(e);");
    gold.append("\n  });");
    gold.append("\n  $.TP('foo_scope', function(a,b,c) {");
    gold.append("\n    var e=$.X();");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    var h=$.pI(b,'fooscope');");
    gold.append("\n    c(g,h,'');");
    gold.append("\n    a.append(g);");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','should_be_canonical_test_for_schema_gen'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    var i=$.pIE(a,'child', true);");
    gold.append("\n    var j=$.E('button');");
    gold.append("\n    var k=[];");
    gold.append("\n    k.push($.bS(j,$.pV(i),'x',1));");
    gold.append("\n    $.onB(j,'click',i,k);");
    gold.append("\n    j.append($.T('Button In Child'));");
    gold.append("\n    g.append(j);");
    gold.append("\n    var j=$.E('div');");
    gold.append("\n    $.UT(j,i,'foo', function(l,m,n) {");
    gold.append("\n      l.append($.T(' Fragment 1 '));");
    gold.append("\n    });");
    gold.append("\n    g.append(j);");
    gold.append("\n    b.append(g);");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    $.IT(g,a,'children',true,function(j) {");
    gold.append("\n      var l=$.E('div');");
    gold.append("\n      var m=$.E('button');");
    gold.append("\n      var n=[];");
    gold.append("\n      n.push($.bS(m,$.pV(j),'z','x'));");
    gold.append("\n      $.onB(m,'click',j,n);");
    gold.append("\n      m.append($.T('Button In Children'));");
    gold.append("\n      l.append(m);");
    gold.append("\n      var m=$.E('div');");
    gold.append("\n      $.UT(m,j,'foo', function(o,p,q) {");
    gold.append("\n        o.append($.T(' Fragment 2 '));");
    gold.append("\n      });");
    gold.append("\n      l.append(m);");
    gold.append("\n      return l;");
    gold.append("\n    });");
    gold.append("\n    b.append(g);");
    gold.append("\n    b.append($.L(a,'should_no_be_in_view'));");
    gold.append("\n    b.append($.L($.pV(a),'w'));");
    gold.append("\n    var g=$.E('button');");
    gold.append("\n    var j=[];");
    gold.append("\n    j.push($.bS(g,$.pV(a),'w',1));");
    gold.append("\n    $.onB(g,'click',a,j);");
    gold.append("\n    g.append($.T('Button Root'));");
    gold.append("\n    b.append(g);");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    $.UT(g,a,'foo', function(l,m,o) {");
    gold.append("\n      l.append($.T(' Fragment 3 '));");
    gold.append("\n    });");
    gold.append("\n    b.append(g);");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    $.UT(g,a,'foo_scope', function(l,m,o) {");
    gold.append("\n      var p=$.E('button');");
    gold.append("\n      var q=[];");
    gold.append("\n      q.push($.bS(p,$.pV(m),'v',1));");
    gold.append("\n      $.onB(p,'click',m,q);");
    gold.append("\n      p.append($.T('Fragment Button'));");
    gold.append("\n      l.append(p);");
    gold.append("\n    });");
    gold.append("\n    b.append(g);");
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
    source.append("\n    <page uri=\"/should_be_canonical_test_for_schema_gen\">");
    source.append("\n        <div rx:scope=\"child\" rx:expand-view-state>");
    source.append("\n            <button rx:click=\"set:x=1\">Button In Child</button>");
    source.append("\n            <div rx:template=\"foo\"> Fragment 1 </div>");
    source.append("\n        </div>");
    source.append("\n        <div rx:iterate=\"children\" rx:expand-view-state>");
    source.append("\n            <button rx:click=\"set:z=x\">Button In Children</button>");
    source.append("\n            <div rx:template=\"foo\"> Fragment 2 </div>");
    source.append("\n        </div>");
    source.append("\n        <lookup path=\"should_no_be_in_view\" />");
    source.append("\n        <lookup path=\"view:w\" />");
    source.append("\n        <button rx:click=\"set:w=1\">Button Root</button>");
    source.append("\n        <div rx:template=\"foo\"> Fragment 3 </div>");
    source.append("\n        <div rx:template=\"foo_scope\">");
    source.append("\n            <button rx:click=\"set:v=1\">Fragment Button</button>");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n    <template name=\"foo\">");
    source.append("\n        <button rx:click=\"set:y=3\">Set Y</button>");
    source.append("\n    </template>");
    source.append("\n    <template name=\"foo_scope\">");
    source.append("\n        <div rx:scope=\"fooscope\">");
    source.append("\n            <fragment />");
    source.append("\n        </div>");
    source.append("\n    </template>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/should_be_canonical_test_for_schema_gen\" : {");
    gold.append("\n    \"child\" : {");
    gold.append("\n      \"x\" : \"int\",");
    gold.append("\n      \"y\" : \"int\"");
    gold.append("\n    },");
    gold.append("\n    \"children\" : {");
    gold.append("\n      \"#items\" : {");
    gold.append("\n        \"y\" : \"int\",");
    gold.append("\n        \"z\" : \"string\"");
    gold.append("\n      }");
    gold.append("\n    },");
    gold.append("\n    \"v\" : \"int\",");
    gold.append("\n    \"w\" : \"int\",");
    gold.append("\n    \"y\" : \"int\"");
    gold.append("\n  }");
    gold.append("\n}");
    return gold.toString();
  }
}
