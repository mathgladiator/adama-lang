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

public class TemplateDomainConnectionTests extends BaseRxHtmlTest {
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
    gold.append("\n    d.name='myname';");
    gold.append("\n    d.identity=true;");
    gold.append("\n    d.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,d);");
    gold.append("\n    d.__();");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='myname';");
    gold.append("\n    $.P(b,a,e,function(b,f) {");
    gold.append("\n      b.append($.T(' NEAT '));");
    gold.append("\n    },function(b,f) {");
    gold.append("\n    });");
    gold.append("\n    e.__();");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.name='myname';");
    gold.append("\n    d.identity=true;");
    gold.append("\n    d.redirect='/sign-in';");
    gold.append("\n    $.DCONNECT(a,d);");
    gold.append("\n    d.__();");
    gold.append("\n    var e=$.RX([]);");
    gold.append("\n    e.name='myname';");
    gold.append("\n    $.P(b,a,e,function(b,f) {");
    gold.append("\n      b.append($.T(' NEAT '));");
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
    gold.append("\n</style></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <connection name=\"myname\" use-domain>");
    source.append("\n            NEAT");
    source.append("\n        </connection>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
