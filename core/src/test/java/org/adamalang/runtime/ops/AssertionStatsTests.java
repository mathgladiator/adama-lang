/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.ops;

import org.junit.Assert;
import org.junit.Test;

public class AssertionStatsTests {
  @Test
  public void coverage() {
    final var stats = new AssertionStats(52, 1);
    Assert.assertEquals(52, stats.total);
    Assert.assertEquals(1, stats.failures);
  }
}
