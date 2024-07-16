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

public class NtComplexTests {
  @Test
  public void equals() {
    NtComplex a = new NtComplex(1.2, 3.4);
    NtComplex b = new NtComplex(1.2, 3.4);
    NtComplex c = new NtComplex(3.4, -1.2);
    Assert.assertEquals(a, a);
    Assert.assertEquals(a, b);
    Assert.assertNotEquals(a, c);
    Assert.assertNotEquals(a, "z");
    Assert.assertNotEquals("z", a);
    Assert.assertEquals(-25689151, a.hashCode());
  }

  @Test
  public void compare() {
    NtComplex a = new NtComplex(1.2, 3.4);
    NtComplex b = new NtComplex(1.2, 3.4);
    NtComplex c = new NtComplex(3.4, -1.2);
    Assert.assertEquals(a.compareTo(b), -b.compareTo(a));
    Assert.assertEquals(a.compareTo(c), -c.compareTo(a));
    Assert.assertEquals(0, a.compareTo(a));
  }

  @Test
  public void str() {
    NtComplex a = new NtComplex(1.2, 3.4);
    Assert.assertEquals("1.2 3.4i", a.toString());
    Assert.assertEquals(16, a.memory());
    Assert.assertEquals("0.09230769230769231 -0.26153846153846155i", a.recip().toString());
  }

  @Test
  public void zero() {
    Assert.assertTrue(new NtComplex(0.0, 0.0).zero());
  }
}
