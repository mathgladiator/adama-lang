/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.ops;

import java.util.ArrayList;
import java.util.HashMap;
import org.adamalang.runtime.contracts.DocumentMonitor;

/** a monitor which dumps fun information out to stdout */
public class StdOutDocumentMonitor implements DocumentMonitor {
  public static class TableRegister {
    public int calls;
    public final String colummName;
    public int effectiveness;
    public final String tableName;
    public int total;

    public TableRegister(final String tableName, final String colummName) {
      this.tableName = tableName;
      this.colummName = colummName;
      total = 0;
      effectiveness = 0;
      calls = 0;
    }
  }

  public HashMap<String, TableRegister> stats;

  public StdOutDocumentMonitor() {
    stats = new HashMap<>();
  }

  @Override
  public void assertFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition, final int total, final int failures) {
  }

  public void dump() {
    final var items = new ArrayList<>(stats.values());
    items.sort((a, b) -> {
      final var s0 = Integer.compare(a.total, b.total);
      if (s0 == 0) { return Integer.compare(a.effectiveness, b.effectiveness); }
      return s0;
    });
    System.out.println("table,column,calls,total,effectiveness,%");
    for (final TableRegister tr : items) {
      System.out.println(tr.tableName + "," + tr.colummName + "," + tr.calls + "," + tr.total + "," + tr.effectiveness + "," + tr.effectiveness / (double) tr.total);
    }
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

  @Override
  public void registerTableColumnIndexEffectiveness(final String tableName, final String colummName, final int total, final int effectiveness) {
    final var key = tableName + ":" + colummName;
    var r = stats.get(key);
    if (r == null) {
      r = new TableRegister(tableName, colummName);
      stats.put(key, r);
    }
    r.calls++;
    r.total += total;
    r.effectiveness += effectiveness;
  }

  @Override
  public boolean shouldMeasureTableColumnIndexEffectiveness() {
    return true;
  }
}
