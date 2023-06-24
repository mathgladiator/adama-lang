/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.contracts;

/** a document can be monitored */
public interface DocumentMonitor {
  /** an assertion failure happened */
  void assertFailureAt(int startLine, int startPosition, int endLine, int endLinePosition, int total, int failures);

  /** a goodwill failure happened (i.e an infinite loop) */
  void goodwillFailureAt(int startLine, int startPosition, int endLine, int endLinePosition);

  /** a function has returned, and here is the timing for it */
  void pop(long time, boolean exception);

  /** push a function on the call stack to measure timing */
  void push(String label);

  /** register a table's effectiveness for a particular query */
  void registerTableColumnIndexEffectiveness(String tableName, String colummName, int total, int effectiveness);

  /** should the runtime measure table indexing effectiveness. Warning: this is very slow */
  boolean shouldMeasureTableColumnIndexEffectiveness();
}
