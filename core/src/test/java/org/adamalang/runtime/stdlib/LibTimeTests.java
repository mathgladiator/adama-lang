/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

  @Test
  public void cylicAdd_Battery() {
    {
      NtTime x = LibTime.cyclicAdd(new NtTime(13, 00), new NtTimeSpan(70));
      Assert.assertEquals(13 * 60 + 1, LibTime.toInt(x));
      Assert.assertEquals(13, x.hour);
      Assert.assertEquals(1, x.minute);
    }
    {
      NtTime x = LibTime.cyclicAdd(new NtTime(13, 00), new NtTimeSpan(24 * 60 * 60 * 4));
      Assert.assertEquals(940, LibTime.toInt(x));
      Assert.assertEquals(15, x.hour);
      Assert.assertEquals(40, x.minute);
    }
    {
      NtTime x = LibTime.cyclicAdd(new NtTime(13, 00), new NtTimeSpan(-24 * 60 * 60 * 4));
      Assert.assertEquals(620, LibTime.toInt(x));
      Assert.assertEquals(10, x.hour);
      Assert.assertEquals(20, x.minute);
    }
  }

  @Test
  public void make() {
    NtTime t = LibTime.make(23, 59).get();
    Assert.assertEquals(23, t.hour);
    Assert.assertEquals(59, t.minute);

    Assert.assertFalse(LibTime.make(42, 1).has());
    Assert.assertFalse(LibTime.make(-42, 1).has());
    Assert.assertFalse(LibTime.make(1, 90).has());
    Assert.assertFalse(LibTime.make(1, -90).has());
  }
}
