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
