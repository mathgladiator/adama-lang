/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
