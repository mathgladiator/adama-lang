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
    Assert.assertEquals(ci, ci);
    Assert.assertEquals(1172816919, ci.hashCode());
    Assert.assertEquals(0, ci.compareTo(ci));
    Assert.assertFalse(ci.equals(""));
    Assert.assertFalse(ci.equals(null));
    Assert.assertTrue(ci.equals(new CapacityInstance("space", "region", "machine", true)));
  }

  @Test
  public void ordering() {
    {
      CapacityInstance A = new CapacityInstance("space", "regionA", "machine", true);
      CapacityInstance B = new CapacityInstance("space", "regionB", "machine", true);
      Assert.assertEquals(-1, A.compareTo(B));
      Assert.assertEquals(1, B.compareTo(A));
    }

    {
      CapacityInstance A = new CapacityInstance("space", "region", "machineA", true);
      CapacityInstance B = new CapacityInstance("space", "region", "machineB", true);
      Assert.assertEquals(-1, A.compareTo(B));
      Assert.assertEquals(1, B.compareTo(A));
    }

    {
      CapacityInstance A = new CapacityInstance("spaceA", "region", "machine", true);
      CapacityInstance B = new CapacityInstance("spaceB", "region", "machine", true);
      Assert.assertEquals(-1, A.compareTo(B));
      Assert.assertEquals(1, B.compareTo(A));
    }

  }
}
