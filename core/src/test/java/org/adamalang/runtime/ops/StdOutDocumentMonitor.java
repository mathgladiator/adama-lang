/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.adamalang.runtime.contracts.DocumentMonitor;

import java.util.ArrayList;
import java.util.HashMap;

/** a monitor which dumps fun information out to stdout */
public class StdOutDocumentMonitor implements DocumentMonitor {
  public HashMap<String, TableRegister> stats;

  public StdOutDocumentMonitor() {
    stats = new HashMap<>();
  }

  @Override
  public void assertFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition, final int total, final int failures) {
  }

  @Override
  public void goodwillFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
  }

  @Override
  public void pop(final long time, final boolean exception) {
    System.out.println("POP:" + exception);
  }

  @Override
  public void push(final String label) {
    System.out.println("PUSH:" + label);
  }

  public void dump() {
    final var items = new ArrayList<>(stats.values());
    items.sort((a, b) -> {
      final var s0 = Integer.compare(a.total, b.total);
      if (s0 == 0) {
        return Integer.compare(a.effectiveness, b.effectiveness);
      }
      return s0;
    });
    System.out.println("table,column,calls,total,effectiveness,%");
    for (final TableRegister tr : items) {
      System.out.println(tr.tableName + "," + tr.colummName + "," + tr.calls + "," + tr.total + "," + tr.effectiveness + "," + tr.effectiveness / (double) tr.total);
    }
  }

  public static class TableRegister {
    public final String colummName;
    public final String tableName;
    public int calls;
    public int effectiveness;
    public int total;

    public TableRegister(final String tableName, final String colummName) {
      this.tableName = tableName;
      this.colummName = colummName;
      total = 0;
      effectiveness = 0;
      calls = 0;
    }
  }
}
