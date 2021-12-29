/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.contracts;

/** a document can be monitored */
public interface DocumentMonitor {
  /** an assertion failure happened */
  public void assertFailureAt(int startLine, int startPosition, int endLine, int endLinePosition, int total, int failures);
  /** a goodwill failure happened (i.e an infinite loop) */
  public void goodwillFailureAt(int startLine, int startPosition, int endLine, int endLinePosition);
  /** a function has returned, and here is the timing for it */
  public void pop(long time, boolean exception);
  /** push a function on the call stack to measure timing */
  public void push(String label);
  /** register a table's effectiveness for a particular query */
  public void registerTableColumnIndexEffectiveness(String tableName, String colummName, int total, int effectiveness);
  /** should the runtime measure table indexing effectiveness. Warning: this is
   * very slow */
  public boolean shouldMeasureTableColumnIndexEffectiveness();
}
