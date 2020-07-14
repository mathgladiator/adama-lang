/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.ops;

import org.adamalang.runtime.contracts.DocumentMonitor;

/** a monitor which does nothing */
public class SilentDocumentMonitor implements DocumentMonitor {
  @Override
  public void assertFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition, final int total, final int failures) {
  }

  @Override
  public void goodwillFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
  }

  @Override
  public void pop(final long time, final boolean exception) {
  }

  @Override
  public void push(final String label) {
  }

  @Override
  public void registerTableColumnIndexEffectiveness(final String tableName, final String colummName, final int total, final int effectiveness) {
  }

  @Override
  public boolean shouldMeasureTableColumnIndexEffectiveness() {
    return false;
  }
}
