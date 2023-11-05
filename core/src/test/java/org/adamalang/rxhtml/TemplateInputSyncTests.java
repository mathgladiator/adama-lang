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
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\" ln:ch=\"0;0;0;14;inputSync.rx.html\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <input name=\"email\" rx:sync=\"foo\" rx:debounce=\"zoop\" ln:ch=\"1;1;1;54;inputSync.rx.html\">");
    gold.append("\n    var d=$.E('input');");
    gold.append("\n    $.SA(d,'name',\"email\");");
    gold.append("\n    b.append(d);");
    gold.append("\n    $.SY(d,$.pV(a),'foo',50.0);");
    gold.append("\n");
    gold.append("\n    // <textarea name=\"email\" rx:sync=\"data:foo\" ln:ch=\"2;1;2;43;inputSync.rx.html\">");
    gold.append("\n    var d=$.E('textarea');");
    gold.append("\n    $.SA(d,'name',\"email\");");
    gold.append("\n    b.append(d);");
    gold.append("\n    $.SY(d,$.pD(a),'foo',100.0);");
    gold.append("\n");
    gold.append("\n    // <select name=\"email\" rx:sync=\"view:foo\" ln:ch=\"3;1;3;41;inputSync.rx.html\">");
    gold.append("\n    var d=$.E('select');");
    gold.append("\n    $.SA(d,'name',\"email\");");
    gold.append("\n");
    gold.append("\n    // <option value=\"foo\" ln:ch=\"3;42;3;62;inputSync.rx.html\">");
    gold.append("\n    var e=$.E('option');");
    gold.append("\n    e.value=\"foo\";");
    gold.append("\n    e.append($.T('FOOO'));");
    gold.append("\n    d.append(e);");
    gold.append("\n    b.append(d);");
    gold.append("\n    $.SY(d,$.pV(a),'foo',100.0);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\" ln:ch=\"0;0;0;14;inputSync.rx.html\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <input name=\"email\" rx:sync=\"foo\" rx:debounce=\"zoop\" ln:ch=\"1;1;1;54;inputSync.rx.html\">");
    gold.append("\n    var d=$.E('input');");
    gold.append("\n    $.SA(d,'name',\"email\");");
    gold.append("\n    b.append(d);");
    gold.append("\n    $.SY(d,$.pV(a),'foo',50.0);");
    gold.append("\n");
    gold.append("\n    // <textarea name=\"email\" rx:sync=\"data:foo\" ln:ch=\"2;1;2;43;inputSync.rx.html\">");
    gold.append("\n    var d=$.E('textarea');");
    gold.append("\n    $.SA(d,'name',\"email\");");
    gold.append("\n    b.append(d);");
    gold.append("\n    $.SY(d,$.pD(a),'foo',100.0);");
    gold.append("\n");
    gold.append("\n    // <select name=\"email\" rx:sync=\"view:foo\" ln:ch=\"3;1;3;41;inputSync.rx.html\">");
    gold.append("\n    var d=$.E('select');");
    gold.append("\n    $.SA(d,'name',\"email\");");
    gold.append("\n");
    gold.append("\n    // <option value=\"foo\" ln:ch=\"3;42;3;62;inputSync.rx.html\">");
    gold.append("\n    var e=$.E('option');");
    gold.append("\n    e.value=\"foo\";");
    gold.append("\n    e.append($.T('FOOO'));");
    gold.append("\n    d.append(e);");
    gold.append("\n    b.append(d);");
    gold.append("\n    $.SY(d,$.pV(a),'foo',100.0);");
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
    source.append("\n        <input name=\"email\" rx:sync=\"foo\" rx:debounce=\"zoop\"/>");
    source.append("\n        <textarea name=\"email\" rx:sync=\"data:foo\" ></textarea>");
    source.append("\n        <select name=\"email\" rx:sync=\"view:foo\">");
    source.append("\n            <option value=\"foo\">FOOO</option>");
    source.append("\n        </select>");
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
