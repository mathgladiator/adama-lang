/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.mocks;

import org.adamalang.common.TimeSource;
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
