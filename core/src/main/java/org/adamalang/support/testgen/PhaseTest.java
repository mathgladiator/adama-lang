/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class PhaseTest {
    public static void go(LivingDocumentFactory factory, DocumentMonitor monitor, AtomicBoolean passedTests, StringBuilder outputFile) throws Exception {
        outputFile.append("--JAVA TEST RESULTS--------------------------------").append("\n");
        final var report = new TestReportBuilder();
        factory.populateTestReport(report, monitor, "42");
        if (report.toString().contains("HAS FAILURES")) {
            passedTests.set(false);
        }
        outputFile.append(report.toString()).append("\n");
    }
}
