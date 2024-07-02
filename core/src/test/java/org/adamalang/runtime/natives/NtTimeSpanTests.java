/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtTimeSpanTests {
  @Test
  public void coverage() {
    NtTimeSpan ts = new NtTimeSpan(123);
    Assert.assertEquals(ts, ts);
    Assert.assertEquals(ts, new NtTimeSpan(123));
    Assert.assertNotEquals(ts, new NtTimeSpan(423));
    Assert.assertNotEquals(ts, "");
    Assert.assertNotEquals(ts, null);
    ts.hashCode();
    Assert.assertEquals("123.0 sec", ts.toString());
    Assert.assertEquals(24, ts.memory());
  }

  @Test
  public void compare() {
    NtTimeSpan a = new NtTimeSpan(1);
    NtTimeSpan b = new NtTimeSpan(54);
    Assert.assertTrue(a.compareTo(b) < 0);
    Assert.assertTrue(b.compareTo(a) > 0);
    Assert.assertEquals(0, a.compareTo(a));
  }
}
