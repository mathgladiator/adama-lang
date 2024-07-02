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

public class NtDateTests {
  @Test
  public void coverage() {
    NtDate d = new NtDate(2010, 11, 22);
    Assert.assertEquals(d, d);
    Assert.assertEquals(d, new NtDate(2010, 11, 22));
    Assert.assertNotEquals(d, new NtDate(2222, 11, 22));
    Assert.assertNotEquals(d, new NtDate(2010, 7, 22));
    Assert.assertNotEquals(d, new NtDate(2010, 11, 18));
    Assert.assertNotEquals(d, "");
    Assert.assertNotEquals(d, null);
    d.hashCode();
    Assert.assertEquals("2010-11-22", d.toString());
    Assert.assertEquals(24, d.memory());
  }

  @Test
  public void toint() {
    NtDate d = new NtDate(2010, 11, 22);
    Assert.assertEquals(23541494, d.toInt());
  }

  @Test
  public void compare_year() {
    Assert.assertEquals(0, new NtDate(2010, 11, 22).compareTo(new NtDate(2010, 11, 22)));
    Assert.assertEquals(-1, new NtDate(2010, 11, 22).compareTo(new NtDate(2011, 11, 22)));
    Assert.assertEquals(1, new NtDate(2011, 11, 22).compareTo(new NtDate(2010, 11, 22)));
  }

  @Test
  public void compare_month() {
    Assert.assertEquals(-1, new NtDate(2010, 10, 22).compareTo(new NtDate(2010, 11, 22)));
    Assert.assertEquals(1, new NtDate(2010, 12, 22).compareTo(new NtDate(2010, 11, 22)));
  }

  @Test
  public void compare_day() {
    Assert.assertEquals(-1, new NtDate(2010, 11, 21).compareTo(new NtDate(2010, 11, 22)));
    Assert.assertEquals(1, new NtDate(2010, 11, 23).compareTo(new NtDate(2010, 11, 22)));
  }
}
