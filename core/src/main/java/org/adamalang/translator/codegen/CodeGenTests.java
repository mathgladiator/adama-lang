/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
        sb.append("public void __test_").append(test.name).append("(TestReportBuilder report) {").tabUp().writeNewline();
        sb.append("report.begin(\"").append(test.name).append("\");").writeNewline();
        test.code.writeJava(sb, environment.scopeAsUnitTest());
        sb.writeNewline();
        sb.append("report.end(getAndResetAssertions());").tabDown().writeNewline();
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
      sb.append("public void __test(TestReportBuilder report, String testName) {").tabUp().writeNewline();
      sb.append("switch(testName) {").writeNewline();
      for (final DefineTest test : environment.document.tests) {
        sb.tab().append("case \"").append(test.name).append("\":").writeNewline();
        sb.tab().tab().append("  __test_").append(test.name).append("(report);").writeNewline();
        sb.tab().tab().append("  return;").writeNewline();
      }
      sb.append("}").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else {
      sb.append("public void __test(TestReportBuilder report, String testName) {}").writeNewline();
    }
  }
}
