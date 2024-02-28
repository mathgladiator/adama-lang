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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineTest;

/** responsible for writing tests */
public class CodeGenTests {
  public static void writeTests(final StringBuilderWithTabs sb, final Environment environment) {
    // generate test bodies
    if (!environment.state.options.removeTests) {
      for (final DefineTest test : environment.document.tests) {
        sb.append("public void __test_").append(test.name).append("(TestReportBuilder __report) throws AbortMessageException {").tabUp().writeNewline();
        sb.append("__report.begin(\"").append(test.name).append("\");").writeNewline();
        sb.append("try ");
        test.code.writeJava(sb, environment.scopeAsUnitTest());
        sb.append(" finally {").tabUp().writeNewline();
        sb.append("__report.end(getAndResetAssertions());").tabDown().writeNewline();
        sb.append("}").tabDown().writeNewline();
        sb.append("}").writeNewline();
      }
    }
    sb.append("@Override").writeNewline();
    sb.append("public String[] __getTests() {").tabUp().writeNewline();
    sb.append("return new String[] {");
    if (!environment.state.options.removeTests) {
      var first = true;
      for (final DefineTest test : environment.document.tests) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append("\"").append(test.name).append("\"");
      }
    }
    sb.append("};").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    if (environment.document.tests.size() > 0 && !environment.state.options.removeTests) {
      sb.append("public void __test(TestReportBuilder report, String testName) throws AbortMessageException {").tabUp().writeNewline();
      sb.append("switch(testName) {").writeNewline();
      for (final DefineTest test : environment.document.tests) {
        sb.tab().append("case \"").append(test.name).append("\":").writeNewline();
        sb.tab().tab().append("  __test_").append(test.name).append("(report);").writeNewline();
        sb.tab().tab().append("  return;").writeNewline();
      }
      sb.append("}").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else {
      sb.append("public void __test(TestReportBuilder report, String testName) throws AbortMessageException {}").writeNewline();
    }
  }
}
