/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.support.testgen;

import java.util.concurrent.atomic.AtomicBoolean;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.translator.jvm.LivingDocumentFactory;

public class PhaseTest {
  public static void go(final LivingDocumentFactory factory, final DocumentMonitor monitor, final AtomicBoolean passedTests, final StringBuilder outputFile) throws Exception {
    outputFile.append("--JAVA TEST RESULTS--------------------------------").append("\n");
    final var report = new TestReportBuilder();
    factory.populateTestReport(report, monitor, "42");
    if (report.toString().contains("HAS FAILURES")) {
      passedTests.set(false);
    }
    outputFile.append(report.toString()).append("\n");
  }
}
