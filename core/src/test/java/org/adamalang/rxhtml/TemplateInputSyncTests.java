/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateInputSyncTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:/:zoop should be a numeric value");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('input');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pV(a),'foo',50.0);");
    gold.append("\n    var c = $.E('textarea');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pD(a),'foo',100.0);");
    gold.append("\n    var c = $.E('select');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    var d = $.E('option');");
    gold.append("\n    d.value=\"foo\";");
    gold.append("\n    d.append($.T('FOOO'));");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pV(a),'foo',100.0);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('input');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pV(a),'foo',50.0);");
    gold.append("\n    var c = $.E('textarea');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pD(a),'foo',100.0);");
    gold.append("\n    var c = $.E('select');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    var d = $.E('option');");
    gold.append("\n    d.value=\"foo\";");
    gold.append("\n    d.append($.T('FOOO'));");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pV(a),'foo',100.0);");
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
    source.append("\n        <input name=\"email\" rx:sync=\"foo\" rx:debounce=\"zoop\"/>");
    source.append("\n        <textarea name=\"email\" rx:sync=\"data:foo\" ></textarea>");
    source.append("\n        <select name=\"email\" rx:sync=\"view:foo\">");
    source.append("\n            <option value=\"foo\">FOOO</option>");
    source.append("\n        </select>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
