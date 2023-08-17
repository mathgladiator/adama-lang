/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.capacity;

import org.junit.Assert;
import org.junit.Test;

public class CapacityInstanceTests {
  @Test
  public void trivial() {
    CapacityInstance ci = new CapacityInstance("space", "region", "machine", true);
    Assert.assertEquals("space", ci.space);
    Assert.assertEquals("region", ci.region);
    Assert.assertEquals("machine", ci.machine);
    Assert.assertTrue(ci.override);
  }
}
