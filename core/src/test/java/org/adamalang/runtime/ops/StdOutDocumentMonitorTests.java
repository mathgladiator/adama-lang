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

import org.junit.Test;

public class StdOutDocumentMonitorTests {
  @Test
  public void coverage() {
    final var monitor = new StdOutDocumentMonitor();
    monitor.assertFailureAt(0, 0, 0, 0, 0, 0);
    monitor.goodwillFailureAt(0, 0, 0, 0);
    monitor.push("hi");
    monitor.pop(0, false);
    monitor.shouldMeasureTableColumnIndexEffectiveness();
    monitor.registerTableColumnIndexEffectiveness("nope", "nope", 100, 5);
    monitor.registerTableColumnIndexEffectiveness("nope", "3", 100, 5);
    monitor.registerTableColumnIndexEffectiveness("a", "nope", 100, 5);
    monitor.registerTableColumnIndexEffectiveness("nope", "a", 100, 5);
    monitor.registerTableColumnIndexEffectiveness("1", "a", 100, 5);
    monitor.registerTableColumnIndexEffectiveness("1", "b", 52, 5);
    monitor.registerTableColumnIndexEffectiveness("1", "b", 74, 5);
    monitor.registerTableColumnIndexEffectiveness("1", "c", 74, 4);
    monitor.registerTableColumnIndexEffectiveness("1", "d", 74, 2);
    monitor.registerTableColumnIndexEffectiveness("1", "b", 74, 6776);
    monitor.dump();
  }
}
