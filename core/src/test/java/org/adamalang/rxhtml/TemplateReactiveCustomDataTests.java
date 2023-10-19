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

public class TemplateReactiveCustomDataTests extends BaseRxHtmlTest {
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
    gold.append("\n    var d=$.RX(['two','three']);");
    gold.append("\n    d.one='one';");
    gold.append("\n    $.Y2(a,d,'two','var',function(e) {");
    gold.append("\n      d.two=e['var']");
    gold.append("\n      d.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2(a,d,'three','var1',function(e) {");
    gold.append("\n      d.three=e['var1'] + e['var2']");
    gold.append("\n      d.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2(a,d,'three','var2',function(e) {");
    gold.append("\n      d.three=e['var1'] + e['var2']");
    gold.append("\n      d.__();");
    gold.append("\n    });");
    gold.append("\n    d.present='';");
    gold.append("\n    $.CUDA(b,a,'',d,'/sign-in',function(e) {");
    gold.append("\n    });");
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
    gold.append("\n    var d=$.RX(['two','three']);");
    gold.append("\n    d.one='one';");
    gold.append("\n    $.Y2(a,d,'two','var',function(e) {");
    gold.append("\n      d.two=e['var']");
    gold.append("\n      d.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2(a,d,'three','var1',function(e) {");
    gold.append("\n      d.three=e['var1'] + e['var2']");
    gold.append("\n      d.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2(a,d,'three','var2',function(e) {");
    gold.append("\n      d.three=e['var1'] + e['var2']");
    gold.append("\n      d.__();");
    gold.append("\n    });");
    gold.append("\n    d.present='';");
    gold.append("\n    $.CUDA(b,a,'',d,'/sign-in',function(e) {");
    gold.append("\n    });");
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
    source.append("\n        <customdata parameter:one=\"one\" parameter:two=\"{var}\"  parameter:three=\"{var1}{var2}\" parameter:present>");
    source.append("\n");
    source.append("\n        </customdata>");
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
