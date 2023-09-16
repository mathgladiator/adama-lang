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
      report.append("...DUMP:").append(writer).append("\n");
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
