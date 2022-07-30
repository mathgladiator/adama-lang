/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

public class TemplateFormWarningsActionsTests extends BaseRxHtmlTest {
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:The input 'x' is excessive.");
    issues.append("\nWARNING:Failed to find an input for 'email'");
    issues.append("\nWARNING:Failed to find an input for 'password'");
    issues.append("\nWARNING:Failed to find an input for 'remember'");
    issues.append("\nWARNING:Failed to find an input for 'email'");
    issues.append("\nWARNING:Failed to find an input for 'email'");
    issues.append("\nWARNING:Failed to find an input for 'password'");
    issues.append("\nWARNING:Failed to find an input for 'code'");
    issues.append("\nWARNING:Emails should have type 'email'.");
    issues.append("\nWARNING:Passwords should have type 'password'.");
    issues.append("\nWARNING:Failed to find an input for 'remember'");
    issues.append("\nWARNING:Emails should have type 'email'");
    issues.append("\nWARNING:Emails should have type 'email'");
    issues.append("\nWARNING:Passwords should have type 'password'.");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSO(c,a,'default','sign_in_failed','/');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','x');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCC(c,a,'foo','custom_status');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','x');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSU(c,a,'sign_up_failed','/');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','x');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSP(c,a,'set_password_failed','/');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','x');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'channel','send_failed');");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSO(c,a,'default','sign_in_failed','/');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','email');");
    gold.append("\n    c.append(d);");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','password');");
    gold.append("\n    c.append(d);");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('type','submit');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCC(c,a,'foo','custom_status');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('type','submit');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSU(c,a,'sign_up_failed','/');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','email');");
    gold.append("\n    c.append(d);");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('type','submit');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSP(c,a,'set_password_failed','/');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','email');");
    gold.append("\n    c.append(d);");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','password');");
    gold.append("\n    c.append(d);");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','code');");
    gold.append("\n    c.append(d);");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('type','submit');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'channel','send_failed');");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('name','param');");
    gold.append("\n    c.append(d);");
    gold.append("\n    var d = $.E('input');");
    gold.append("\n    d.setAttribute('type','submit');");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        Simple Page");
    source.append("\n        <form rx:action=\"adama:sign-in\">");
    source.append("\n            <input name=\"x\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"custom:foo\">");
    source.append("\n            <input name=\"x\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:sign-up\">");
    source.append("\n            <input name=\"x\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:set-password\">");
    source.append("\n            <input name=\"x\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"send:channel\">");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:sign-in\">");
    source.append("\n            <input name=\"email\" />");
    source.append("\n            <input name=\"password\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"custom:foo\">");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:sign-up\">");
    source.append("\n            <input name=\"email\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:set-password\">");
    source.append("\n            <input name=\"email\" />");
    source.append("\n            <input name=\"password\" />");
    source.append("\n            <input name=\"code\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"send:channel\">");
    source.append("\n            <input name=\"param\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
