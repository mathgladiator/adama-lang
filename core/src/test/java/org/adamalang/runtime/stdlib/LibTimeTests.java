/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtTime;
import org.adamalang.runtime.natives.NtTimeSpan;
import org.junit.Assert;
import org.junit.Test;

public class LibTimeTests {
  @Test
  public void overlaps() {
    Assert.assertTrue(LibTime.overlaps(new NtTime(13, 23), new NtTime(14, 50), new NtTime(13, 40), new NtTime(19, 00)));
    Assert.assertFalse(LibTime.overlaps(new NtTime(13, 23), new NtTime(14, 50), new NtTime(15, 40), new NtTime(19, 00)));
  }

  @Test
  public void extendWithDay_battery() {
    {
      NtTime x = LibTime.extendWithinDay(new NtTime(13, 00), new NtTimeSpan(70));
      Assert.assertEquals(13 * 60 + 1, LibTime.toInt(x));
      Assert.assertEquals(13, x.hour);
      Assert.assertEquals(1, x.minute);
    }
    {
      NtTime x = LibTime.extendWithinDay(new NtTime(13, 00), new NtTimeSpan(24 * 60 * 60 * 4));
      Assert.assertEquals(24 * 60 - 1, LibTime.toInt(x));
      Assert.assertEquals(23, x.hour);
      Assert.assertEquals(59, x.minute);
    }
    {
      NtTime x = LibTime.extendWithinDay(new NtTime(13, 00), new NtTimeSpan(-24 * 60 * 60 * 4));
      Assert.assertEquals(0, LibTime.toInt(x));
      Assert.assertEquals(0, x.hour);
      Assert.assertEquals(0, x.minute);
    }
  }
}
