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

public class NtPrincipalTests {
  @Test
  public void comparisons() {
    final var cv1 = new NtPrincipal("a", "b");
    final var cv2 = new NtPrincipal("b", "b");
    final var cv3 = new NtPrincipal("b", "a");
    final var cv4 = new NtPrincipal("b", "c");
    Assert.assertEquals(-1, cv1.compareTo(cv2));
    Assert.assertEquals(1, cv1.compareTo(cv3));
    Assert.assertEquals(-1, cv1.compareTo(cv4));
    Assert.assertEquals(1, cv2.compareTo(cv1));
    Assert.assertEquals(1, cv2.compareTo(cv3));
    Assert.assertEquals(-1, cv2.compareTo(cv4));
    Assert.assertEquals(-1, cv3.compareTo(cv1));
    Assert.assertEquals(-1, cv3.compareTo(cv2));
    Assert.assertEquals(-2, cv3.compareTo(cv4));
    Assert.assertEquals(1, cv4.compareTo(cv1));
    Assert.assertEquals(1, cv4.compareTo(cv2));
    Assert.assertEquals(2, cv4.compareTo(cv3));
    Assert.assertEquals(0, cv1.compareTo(cv1));
    Assert.assertEquals(0, cv2.compareTo(cv2));
    Assert.assertEquals(0, cv3.compareTo(cv3));
    Assert.assertEquals(0, cv4.compareTo(cv4));
    Assert.assertFalse(cv1.equals(cv2));
    Assert.assertFalse(cv1.equals(cv3));
    Assert.assertFalse(cv1.equals(cv4));
    Assert.assertFalse(cv2.equals(cv1));
    Assert.assertFalse(cv2.equals(cv3));
    Assert.assertFalse(cv2.equals(cv4));
    Assert.assertFalse(cv3.equals(cv1));
    Assert.assertFalse(cv3.equals(cv2));
    Assert.assertFalse(cv3.equals(cv4));
    Assert.assertFalse(cv4.equals(cv1));
    Assert.assertFalse(cv4.equals(cv2));
    Assert.assertFalse(cv4.equals(cv3));
    Assert.assertTrue(cv1.equals(cv1));
    Assert.assertTrue(cv2.equals(cv2));
    Assert.assertTrue(cv3.equals(cv3));
    Assert.assertTrue(cv4.equals(cv4));
    Assert.assertFalse(cv4.equals("sys"));
  }

  @Test
  public void coverage() {
    NtPrincipal.NO_ONE.toString();
    Assert.assertEquals(4, NtPrincipal.NO_ONE.memory());
  }
}
