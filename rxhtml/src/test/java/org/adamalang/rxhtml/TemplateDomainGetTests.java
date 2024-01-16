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

public class TemplateDomainGetTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:/:get:delay not parsed as integer '1500.7'");
    issues.append("\nWARNING:/:get:delay not parsed as integer 'xyz'");
    issues.append("\nWARNING:/:get:delay not parsed as double! 'xyz'");
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
    gold.append("\n    var e=$.E('div');");
    gold.append("\n    b.append(e);");
    gold.append("\n");
    gold.append("\n    // <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    gold.append("\n    var f=$.RX(['v']);");
    gold.append("\n    f.url='/foo/path/blah';");
    gold.append("\n    f.identity=true;");
    gold.append("\n    f.redirect=true;");
    gold.append("\n    $.Y2(a,f,'v','x',function(g) {");
    gold.append("\n      f.v=$.F(g,'x')");
    gold.append("\n      f.__();");
    gold.append("\n    });");
    gold.append("\n    $.DG(e,a,f,function(e,d) {");
    gold.append("\n      e.append($.L(d,'some_name'));");
    gold.append("\n    },function(e,d) {");
    gold.append("\n    },250);");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    b.append(g);");
    gold.append("\n");
    gold.append("\n    // <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    gold.append("\n    var h=$.RX(['v']);");
    gold.append("\n    h.url='/foo/path/blah';");
    gold.append("\n    h.identity=true;");
    gold.append("\n    h.redirect=true;");
    gold.append("\n    $.Y2(a,h,'v','x',function(i) {");
    gold.append("\n      h.v=$.F(i,'x')");
    gold.append("\n      h.__();");
    gold.append("\n    });");
    gold.append("\n    $.DG(g,a,h,function(g,d) {");
    gold.append("\n      g.append($.L(d,'some_name'));");
    gold.append("\n    },function(g,d) {");
    gold.append("\n    },1500);");
    gold.append("\n    var i=$.E('div');");
    gold.append("\n    b.append(i);");
    gold.append("\n");
    gold.append("\n    // <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    gold.append("\n    var j=$.RX(['v']);");
    gold.append("\n    j.url='/foo/path/blah';");
    gold.append("\n    j.identity=true;");
    gold.append("\n    j.redirect=true;");
    gold.append("\n    $.Y2(a,j,'v','x',function(k) {");
    gold.append("\n      j.v=$.F(k,'x')");
    gold.append("\n      j.__();");
    gold.append("\n    });");
    gold.append("\n    $.DG(i,a,j,function(i,d) {");
    gold.append("\n      i.append($.L(d,'some_name'));");
    gold.append("\n    },function(i,d) {");
    gold.append("\n    },1501);");
    gold.append("\n    var k=$.E('div');");
    gold.append("\n    b.append(k);");
    gold.append("\n");
    gold.append("\n    // <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    gold.append("\n    var l=$.RX(['v']);");
    gold.append("\n    l.url='/foo/path/blah';");
    gold.append("\n    l.identity=true;");
    gold.append("\n    l.redirect=true;");
    gold.append("\n    $.Y2(a,l,'v','x',function(m) {");
    gold.append("\n      l.v=$.F(m,'x')");
    gold.append("\n      l.__();");
    gold.append("\n    });");
    gold.append("\n    $.DG(k,a,l,function(k,d) {");
    gold.append("\n      k.append($.L(d,'some_name'));");
    gold.append("\n    },function(k,d) {");
    gold.append("\n    },250);");
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
    gold.append("\n    var e=$.E('div');");
    gold.append("\n    b.append(e);");
    gold.append("\n");
    gold.append("\n    // <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    gold.append("\n    var f=$.RX(['v']);");
    gold.append("\n    f.url='/foo/path/blah';");
    gold.append("\n    f.identity=true;");
    gold.append("\n    f.redirect=true;");
    gold.append("\n    $.Y2(a,f,'v','x',function(g) {");
    gold.append("\n      f.v=$.F(g,'x')");
    gold.append("\n      f.__();");
    gold.append("\n    });");
    gold.append("\n    $.DG(e,a,f,function(e,d) {");
    gold.append("\n      e.append($.L(d,'some_name'));");
    gold.append("\n    },function(e,d) {");
    gold.append("\n    },250);");
    gold.append("\n    var g=$.E('div');");
    gold.append("\n    b.append(g);");
    gold.append("\n");
    gold.append("\n    // <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    gold.append("\n    var h=$.RX(['v']);");
    gold.append("\n    h.url='/foo/path/blah';");
    gold.append("\n    h.identity=true;");
    gold.append("\n    h.redirect=true;");
    gold.append("\n    $.Y2(a,h,'v','x',function(i) {");
    gold.append("\n      h.v=$.F(i,'x')");
    gold.append("\n      h.__();");
    gold.append("\n    });");
    gold.append("\n    $.DG(g,a,h,function(g,d) {");
    gold.append("\n      g.append($.L(d,'some_name'));");
    gold.append("\n    },function(g,d) {");
    gold.append("\n    },1500);");
    gold.append("\n    var i=$.E('div');");
    gold.append("\n    b.append(i);");
    gold.append("\n");
    gold.append("\n    // <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    gold.append("\n    var j=$.RX(['v']);");
    gold.append("\n    j.url='/foo/path/blah';");
    gold.append("\n    j.identity=true;");
    gold.append("\n    j.redirect=true;");
    gold.append("\n    $.Y2(a,j,'v','x',function(k) {");
    gold.append("\n      j.v=$.F(k,'x')");
    gold.append("\n      j.__();");
    gold.append("\n    });");
    gold.append("\n    $.DG(i,a,j,function(i,d) {");
    gold.append("\n      i.append($.L(d,'some_name'));");
    gold.append("\n    },function(i,d) {");
    gold.append("\n    },1501);");
    gold.append("\n    var k=$.E('div');");
    gold.append("\n    b.append(k);");
    gold.append("\n");
    gold.append("\n    // <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    gold.append("\n    var l=$.RX(['v']);");
    gold.append("\n    l.url='/foo/path/blah';");
    gold.append("\n    l.identity=true;");
    gold.append("\n    l.redirect=true;");
    gold.append("\n    $.Y2(a,l,'v','x',function(m) {");
    gold.append("\n      l.v=$.F(m,'x')");
    gold.append("\n      l.__();");
    gold.append("\n    });");
    gold.append("\n    $.DG(k,a,l,function(k,d) {");
    gold.append("\n      k.append($.L(d,'some_name'));");
    gold.append("\n    },function(k,d) {");
    gold.append("\n    },250);");
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
    source.append("\n        <domain-get url=\"/foo/path/blah\" search:v=\"{x}\">");
    source.append("\n            <lookup path=\"some_name\" />");
    source.append("\n        </domain-get>");
    source.append("\n");
    source.append("\n        <domain-get url=\"/foo/path/blah\" search:v=\"{x}\" get:delay=\"1500\">");
    source.append("\n            <lookup path=\"some_name\" />");
    source.append("\n        </domain-get>");
    source.append("\n");
    source.append("\n        <domain-get url=\"/foo/path/blah\" search:v=\"{x}\" get:delay=\"1500.7\">");
    source.append("\n            <lookup path=\"some_name\" />");
    source.append("\n        </domain-get>");
    source.append("\n        <domain-get url=\"/foo/path/blah\" search:v=\"{x}\" get:delay=\"xyz\">");
    source.append("\n            <lookup path=\"some_name\" />");
    source.append("\n        </domain-get>");
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
