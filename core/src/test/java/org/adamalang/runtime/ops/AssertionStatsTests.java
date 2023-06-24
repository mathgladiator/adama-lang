/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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
