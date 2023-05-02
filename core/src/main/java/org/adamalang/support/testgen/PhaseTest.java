/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.support.testgen;

import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class PhaseTest {
  public static void go(final LivingDocumentFactory factory, final DocumentMonitor monitor, final AtomicBoolean passedTests, final StringBuilder outputFile) throws Exception {
    outputFile.append("--JAVA TEST RESULTS--------------------------------").append("\n");
    final var report = new TestReportBuilder();
    factory.populateTestReport(report, monitor, "42");
    if (report.toString().contains("HAS FAILURES")) {
      passedTests.set(false);
    }
    outputFile.append(report).append("\n");
  }
}
