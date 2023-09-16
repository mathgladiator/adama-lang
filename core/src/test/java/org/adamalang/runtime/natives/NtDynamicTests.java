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

public class NtDynamicTests {
  @Test
  public void coverage() {
    NtDynamic T = new NtDynamic("true");
    NtDynamic F = new NtDynamic("false");
    Assert.assertEquals("true", T.toString());
    Assert.assertEquals("false", F.toString());
    Assert.assertEquals(14, T.compareTo(F));
    Assert.assertEquals(-14, F.compareTo(T));
    Assert.assertTrue(T.equals(T));
    Assert.assertTrue(F.equals(F));
    Assert.assertTrue(T.equals(new NtDynamic("true")));
    Assert.assertTrue(F.equals(new NtDynamic("false")));
    Assert.assertFalse(T.equals(F));
    Assert.assertFalse(F.equals(T));
    Assert.assertFalse(F.equals(false));
    T.hashCode();
    Assert.assertTrue(NtDynamic.NULL.equals(new NtDynamic("null")));
    Assert.assertEquals(10, F.memory());
    Assert.assertEquals(T, T.to_dynamic());
    Assert.assertTrue(T == T.to_dynamic());
  }

  @Test
  public void cache() {
    NtDynamic T = new NtDynamic("{}");
    Assert.assertTrue(T.cached() == T.cached());
  }
}
