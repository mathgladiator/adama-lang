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
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class NtTimeTests {
  @Test
  public void coverage() {
    NtTime t = new NtTime(12, 17);
    Assert.assertEquals(t, t);
    Assert.assertEquals(t, new NtTime(12, 17));
    Assert.assertNotEquals(t, new NtTime(1, 4));
    Assert.assertNotEquals(t, null);
    Assert.assertNotEquals(t, "");
    t.hashCode();
    Assert.assertEquals("12:17", t.toString());
  }

  @Test
  public void toint() {
    NtTime t = new NtTime(12, 17);
    Assert.assertEquals(737, t.toInt());
  }

  @Test
  public void pad() {
    NtTime t = new NtTime(12, 1);
    Assert.assertEquals("12:01", t.toString());
  }

  @Test
  public void compare() {
    NtTime t1 = new NtTime(12, 1);
    NtTime t2 = new NtTime(12, 5);
    NtTime t3 = new NtTime(11, 1);
    NtTime t4 = new NtTime(13, 1);
    Assert.assertTrue(t1.compareTo(t2) < 0);
    Assert.assertTrue(t2.compareTo(t1) > 0);
    Assert.assertTrue(t1.compareTo(t4) < 0);
    Assert.assertTrue(t3.compareTo(t1) < 0);
  }
}
