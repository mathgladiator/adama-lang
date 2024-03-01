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

public class TemplateTypingSimpleTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:Privacy violation; field 'pri_x' was referenced, but is not visible within the privacy filter:\"private\" allowed:is_admin");
    issues.append("\nWARNING:Privacy violation; field 'ju_x' was referenced, but is not visible within the privacy filter:[\"just_user\"] allowed:is_admin");
    issues.append("\nWARNING:Failed to type 'not_found_x' within '__Root'");
    issues.append("\nWARNING:Privacy violation; field 'pri_x' was referenced, but is not visible within the privacy filter:\"private\" allowed:just_user");
    issues.append("\nWARNING:Privacy violation; field 'adm_x' was referenced, but is not visible within the privacy filter:[\"is_admin\"] allowed:just_user");
    issues.append("\nWARNING:Failed to type 'not_found_x' within '__Root'");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/a\" privacy=\"is_admin\">");
    gold.append("\n  $.PG(['fixed','a'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <connection use-domain=\"\" backend=\"simple\" name=\"default\" redirect=\"/sign-in\">");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='default';");
    gold.append("\n    d.identity=true;");
    gold.append("\n    d.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,d);");
    gold.append("\n    d.__();");
    gold.append("\n");
    gold.append("\n    // <connection use-domain=\"\" backend=\"simple\" name=\"default\" redirect=\"/sign-in\">");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='default';");
    gold.append("\n    $.P(b,a,e,function(b,f) {");
    gold.append("\n      b.append($.T(' Simple root field testing under the is_admin policy '));");
    gold.append("\n      b.append($.L(f,'pub_x'));");
    gold.append("\n      b.append($.L(f,'bub_x'));");
    gold.append("\n      b.append($.L(f,'pri_x'));");
    gold.append("\n      b.append($.L(f,'adm_x'));");
    gold.append("\n      b.append($.L(f,'ju_x'));");
    gold.append("\n      b.append($.L(f,'not_found_x'));");
    gold.append("\n    },function(b,f) {");
    gold.append("\n    },false);");
    gold.append("\n    e.__();");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/b\" privacy=\"just_user\">");
    gold.append("\n  $.PG(['fixed','b'], function(b,a) {");
    gold.append("\n    var f=$.X();");
    gold.append("\n");
    gold.append("\n    // <connection use-domain=\"\" backend=\"simple\" name=\"default\" redirect=\"/sign-in\">");
    gold.append("\n    var g=$.RX([]);");
    gold.append("\n    g.name='default';");
    gold.append("\n    g.identity=true;");
    gold.append("\n    g.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,g);");
    gold.append("\n    g.__();");
    gold.append("\n");
    gold.append("\n    // <connection use-domain=\"\" backend=\"simple\" name=\"default\" redirect=\"/sign-in\">");
    gold.append("\n    var h=$.RX([]);");
    gold.append("\n    h.name='default';");
    gold.append("\n    $.P(b,a,h,function(b,i) {");
    gold.append("\n      b.append($.T(' Simple root field testing under the just user policy '));");
    gold.append("\n      b.append($.L(i,'pub_x'));");
    gold.append("\n      b.append($.L(i,'bub_x'));");
    gold.append("\n      b.append($.L(i,'pri_x'));");
    gold.append("\n      b.append($.L(i,'adm_x'));");
    gold.append("\n      b.append($.L(i,'ju_x'));");
    gold.append("\n      b.append($.L(i,'not_found_x'));");
    gold.append("\n    },function(b,i) {");
    gold.append("\n    },false);");
    gold.append("\n    h.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/a\" privacy=\"is_admin\">");
    gold.append("\n  $.PG(['fixed','a'], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <connection use-domain=\"\" backend=\"simple\" name=\"default\" redirect=\"/sign-in\">");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='default';");
    gold.append("\n    d.identity=true;");
    gold.append("\n    d.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,d);");
    gold.append("\n    d.__();");
    gold.append("\n");
    gold.append("\n    // <connection use-domain=\"\" backend=\"simple\" name=\"default\" redirect=\"/sign-in\">");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='default';");
    gold.append("\n    $.P(b,a,e,function(b,f) {");
    gold.append("\n      b.append($.T(' Simple root field testing under the is_admin policy '));");
    gold.append("\n      b.append($.L(f,'pub_x'));");
    gold.append("\n      b.append($.L(f,'bub_x'));");
    gold.append("\n      b.append($.L(f,'pri_x'));");
    gold.append("\n      b.append($.L(f,'adm_x'));");
    gold.append("\n      b.append($.L(f,'ju_x'));");
    gold.append("\n      b.append($.L(f,'not_found_x'));");
    gold.append("\n    },function(b,f) {");
    gold.append("\n    },false);");
    gold.append("\n    e.__();");
    gold.append("\n  });");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/b\" privacy=\"just_user\">");
    gold.append("\n  $.PG(['fixed','b'], function(b,a) {");
    gold.append("\n    var f=$.X();");
    gold.append("\n");
    gold.append("\n    // <connection use-domain=\"\" backend=\"simple\" name=\"default\" redirect=\"/sign-in\">");
    gold.append("\n    var g=$.RX([]);");
    gold.append("\n    g.name='default';");
    gold.append("\n    g.identity=true;");
    gold.append("\n    g.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,g);");
    gold.append("\n    g.__();");
    gold.append("\n");
    gold.append("\n    // <connection use-domain=\"\" backend=\"simple\" name=\"default\" redirect=\"/sign-in\">");
    gold.append("\n    var h=$.RX([]);");
    gold.append("\n    h.name='default';");
    gold.append("\n    $.P(b,a,h,function(b,i) {");
    gold.append("\n      b.append($.T(' Simple root field testing under the just user policy '));");
    gold.append("\n      b.append($.L(i,'pub_x'));");
    gold.append("\n      b.append($.L(i,'bub_x'));");
    gold.append("\n      b.append($.L(i,'pri_x'));");
    gold.append("\n      b.append($.L(i,'adm_x'));");
    gold.append("\n      b.append($.L(i,'ju_x'));");
    gold.append("\n      b.append($.L(i,'not_found_x'));");
    gold.append("\n    },function(b,i) {");
    gold.append("\n    },false);");
    gold.append("\n    h.__();");
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
    source.append("\n    <page uri=\"/a\" privacy=\"is_admin\">");
    source.append("\n        <connection use-domain backend=\"simple\">");
    source.append("\n            Simple root field testing under the is_admin policy");
    source.append("\n            <lookup path=\"pub_x\" />");
    source.append("\n            <lookup path=\"bub_x\" />");
    source.append("\n            <lookup path=\"pri_x\" />");
    source.append("\n            <lookup path=\"adm_x\" />");
    source.append("\n            <lookup path=\"ju_x\" />");
    source.append("\n            <lookup path=\"not_found_x\" />");
    source.append("\n        </connection>");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/b\" privacy=\"just_user\">");
    source.append("\n        <connection use-domain backend=\"simple\">");
    source.append("\n            Simple root field testing under the just user policy");
    source.append("\n            <lookup path=\"pub_x\" />");
    source.append("\n            <lookup path=\"bub_x\" />");
    source.append("\n            <lookup path=\"pri_x\" />");
    source.append("\n            <lookup path=\"adm_x\" />");
    source.append("\n            <lookup path=\"ju_x\" />");
    source.append("\n            <lookup path=\"not_found_x\" />");
    source.append("\n        </connection>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/a\" : { },");
    gold.append("\n  \"/b\" : { }");
    gold.append("\n}");
    return gold.toString();
  }
}
