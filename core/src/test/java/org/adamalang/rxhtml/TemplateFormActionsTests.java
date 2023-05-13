/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

public class TemplateFormActionsTests extends BaseRxHtmlTest {
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
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.rx_forward='/yes';");
    gold.append("\n    $.aSO(c,a,'default','faied_to_signin',d);");
    gold.append("\n    var e = $.E('input');");
    gold.append("\n    e.setAttribute('name','email');");
    gold.append("\n    e.setAttribute('type','email');");
    gold.append("\n    c.append(e);");
    gold.append("\n    var e = $.E('input');");
    gold.append("\n    e.setAttribute('name','password');");
    gold.append("\n    e.setAttribute('type','password');");
    gold.append("\n    c.append(e);");
    gold.append("\n    var e = $.E('input');");
    gold.append("\n    e.setAttribute('name','remember');");
    gold.append("\n    e.setAttribute('type','checkbox');");
    gold.append("\n    c.append(e);");
    gold.append("\n    var e = $.E('input');");
    gold.append("\n    e.setAttribute('type','submit');");
    gold.append("\n    c.append(e);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.rx_forward='/w00t';");
    gold.append("\n    $.aDSO(c,a,'default','docnope',e);");
    gold.append("\n    var f = $.E('input');");
    gold.append("\n    f.setAttribute('type','hidden');");
    gold.append("\n    f.setAttribute('name','space');");
    gold.append("\n    f.value='s ';");
    gold.append("\n    c.append(f);");
    gold.append("\n    var f = $.E('input');");
    gold.append("\n    f.setAttribute('type','hidden');");
    gold.append("\n    f.setAttribute('name','key');");
    gold.append("\n    f.value='k';");
    gold.append("\n    c.append(f);");
    gold.append("\n    var f = $.E('input');");
    gold.append("\n    f.setAttribute('name','username');");
    gold.append("\n    f.setAttribute('type','username');");
    gold.append("\n    c.append(f);");
    gold.append("\n    var f = $.E('input');");
    gold.append("\n    f.setAttribute('name','password');");
    gold.append("\n    f.setAttribute('type','password');");
    gold.append("\n    c.append(f);");
    gold.append("\n    var f = $.E('input');");
    gold.append("\n    f.setAttribute('name','remember');");
    gold.append("\n    f.setAttribute('type','checkbox');");
    gold.append("\n    c.append(f);");
    gold.append("\n    var f = $.E('input');");
    gold.append("\n    f.setAttribute('type','submit');");
    gold.append("\n    c.append(f);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var f=$.RX([]);");
    gold.append("\n    f.space=true;");
    gold.append("\n    f.key=true;");
    gold.append("\n    f.path=true;");
    gold.append("\n    f.rx_forward='/';");
    gold.append("\n    $.aDPUT(c,a,f);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('type','submit');");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCC(c,a,'foo','custom_status');");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('type','submit');");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSU(c,a,'sign_up_failed','/');");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','email');");
    gold.append("\n    g.setAttribute('type','email');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('type','submit');");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSP(c,a,'set_password_failed','/');");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','email');");
    gold.append("\n    g.setAttribute('type','email');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','password');");
    gold.append("\n    g.setAttribute('type','password');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','code');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('type','submit');");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'channel','send_failed');");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','param');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('type','submit');");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCP(c,$.pV(a),'xyz');");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','param');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('type','submit');");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aUP(c,a,'default','sign_in_failed','/');");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','space');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','key');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('type','submit');");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><link rel=\"stylesheet\" href=\"/template.css\"><script src=\"/template.js\"></script></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        Simple Page");
    source.append("\n        <form rx:action=\"adama:sign-in\" rx:failure=\"faied_to_signin\" rx:forward=\"/yes\">");
    source.append("\n            <input name=\"email\" type=\"email\" />");
    source.append("\n            <input name=\"password\" type=\"password\" />");
    source.append("\n            <input name=\"remember\" type=\"checkbox\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"document:sign-in\" rx:failure=\"docnope\" rx:forward=\"/w00t\">");
    source.append("\n            <input type=\"hidden\" name=\"space\" value=\"s \"/>");
    source.append("\n            <input type=\"hidden\" name=\"key\" value=\"k\" />");
    source.append("\n            <input name=\"username\" type=\"username\" />");
    source.append("\n            <input name=\"password\" type=\"password\" />");
    source.append("\n            <input name=\"remember\" type=\"checkbox\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"document:put\">");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"custom:foo\">");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:sign-up\">");
    source.append("\n            <input name=\"email\" type=\"email\"/>");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:set-password\">");
    source.append("\n            <input name=\"email\" type=\"email\" />");
    source.append("\n            <input name=\"password\" type=\"password\" />");
    source.append("\n            <input name=\"code\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"send:channel\">");
    source.append("\n            <input name=\"param\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"copy:xyz\">");
    source.append("\n            <input name=\"param\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:upload-asset\">");
    source.append("\n            <input name=\"space\" />");
    source.append("\n            <input name=\"key\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
