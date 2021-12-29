/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.ops;

import org.junit.Test;

public class SilentDocumentMonitorTests {
  @Test
  public void coverage() {
    final var monitor = new SilentDocumentMonitor();
    monitor.assertFailureAt(0, 0, 0, 0, 0, 0);
    monitor.goodwillFailureAt(0, 0, 0, 0);
    monitor.push("hi");
    monitor.pop(0, false);
    monitor.shouldMeasureTableColumnIndexEffectiveness();
    monitor.registerTableColumnIndexEffectiveness("nope", "nope", 100, 5);
  }
}
