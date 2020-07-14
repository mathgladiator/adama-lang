/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.ops;

import org.junit.Test;

public class StdOutDocumentMonitorTests {
    @Test
    public void coverage() {
        StdOutDocumentMonitor monitor = new StdOutDocumentMonitor();
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
