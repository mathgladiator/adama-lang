/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.ops;

import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.HashMap;

/** a fun way to build test reports */
public class TestReportBuilder {
  private final HashMap<String, Object> dumps;
  private final StringBuilder report;
  private final long started;
  private int failures;

  public TestReportBuilder() {
    report = new StringBuilder();
    started = System.currentTimeMillis();
    dumps = new HashMap<>();
  }

  public void annotate(final String name, final HashMap<String, Object> dump) {
    if (dump.size() > 0) {
      JsonStreamWriter writer = new JsonStreamWriter();
      writer.writeTree(dump);
      report.append("...DUMP:").append(writer.toString()).append("\n");
    }
    dumps.put(name, dump);
  }

  public void begin(final String name) {
    report.append("TEST[").append(name).append("]");
  }

  public void end(final AssertionStats stats) {
    if (stats.total > 0) {
      report
          .append(" = ")
          .append(Math.round((stats.total - stats.failures) * 1000.0 / stats.total) / 10.0)
          .append("%");
      if (stats.failures > 0) {
        report.append(" (HAS FAILURES)");
      }
      report.append("\n");
    } else {
      report.append(" HAS NO ASSERTS\n");
    }
    failures += stats.failures;
  }

  public int getFailures() {
    return failures;
  }

  @Override
  public String toString() {
    return report.toString();
  }
}
