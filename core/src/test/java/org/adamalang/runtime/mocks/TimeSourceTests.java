/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.TimeSource;
import org.junit.Assert;
import org.junit.Test;

public class TimeSourceTests {
  @Test
  public void coverage() throws Exception {
    final var start = TimeSource.REAL_TIME.nowMilliseconds();
    Thread.sleep(5);
    final var stop = TimeSource.REAL_TIME.nowMilliseconds();
    Assert.assertTrue(stop - start >= 4);
  }
}
