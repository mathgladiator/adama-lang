/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.ops;

import java.util.HashMap;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a fun way to build test reports */
public class TestReportBuilder {
  private final HashMap<String, ObjectNode> dumps;
  private int failures;
  private final ObjectNode node;
  private final StringBuilder report;
  private final long started;

  public TestReportBuilder() {
    report = new StringBuilder();
    started = System.currentTimeMillis();
    node = Utility.createObjectNode();
    dumps = new HashMap<>();
  }

  public void annotate(final String name, final ObjectNode dump) {
    if (dump.size() > 0) {
      report.append("...DUMP:").append(dump.toString()).append("\n");
    }
    dumps.put(name, dump);
  }

  public void begin(final String name) {
    report.append("TEST[").append(name).append("]");
  }

  public void end(final AssertionStats stats) {
    if (stats.total > 0) {
      report.append(" = ").append(Math.round((stats.total - stats.failures) * 1000.0 / stats.total) / 10.0).append("%");
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
