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
package org.adamalang.runtime.sys.web;

import org.junit.Assert;
import org.junit.Test;

public class WebFragmentTests {
  @Test
  public void coverage() {
    WebFragment fragment = new WebFragment("0123456789", "x", 5);
    Assert.assertEquals("56789", fragment.tail());
    Assert.assertNull(fragment.val_boolean);
    Assert.assertNull(fragment.val_double);
    Assert.assertNull(fragment.val_int);
    Assert.assertNull(fragment.val_long);
  }

  @Test
  public void parse_bool_true() {
    WebFragment fragment = new WebFragment("0123456789", "true", 5);
    Assert.assertEquals("56789", fragment.tail());
    Assert.assertNotNull(fragment.val_boolean);
    Assert.assertNull(fragment.val_double);
    Assert.assertNull(fragment.val_int);
    Assert.assertNull(fragment.val_long);
    Assert.assertTrue(fragment.val_boolean);
  }

  @Test
  public void parse_bool_false() {
    WebFragment fragment = new WebFragment("0123456789", "false", 5);
    Assert.assertEquals("56789", fragment.tail());
    Assert.assertNotNull(fragment.val_boolean);
    Assert.assertNull(fragment.val_double);
    Assert.assertNull(fragment.val_int);
    Assert.assertNull(fragment.val_long);
    Assert.assertFalse(fragment.val_boolean);
  }

  @Test
  public void parse_int() {
    WebFragment fragment = new WebFragment("0123456789", "123", 5);
    Assert.assertEquals("56789", fragment.tail());
    Assert.assertNull(fragment.val_boolean);
    Assert.assertNotNull(fragment.val_double);
    Assert.assertNotNull(fragment.val_int);
    Assert.assertNotNull(fragment.val_long);
    Assert.assertTrue((fragment.val_double - 123) < 0.01);
    Assert.assertEquals(123, (int) fragment.val_int);
    Assert.assertEquals(123L, (long) fragment.val_long);
  }

  @Test
  public void parse_long() {
    WebFragment fragment = new WebFragment("0123456789", "123123412344", 5);
    Assert.assertEquals("56789", fragment.tail());
    Assert.assertNull(fragment.val_boolean);
    Assert.assertNotNull(fragment.val_double);
    Assert.assertNull(fragment.val_int);
    Assert.assertNotNull(fragment.val_long);
    Assert.assertTrue((fragment.val_double - 1.23123412344E11) < 0.01);
    Assert.assertEquals(123123412344L, (long) fragment.val_long);
  }

  @Test
  public void parse_double() {
    WebFragment fragment = new WebFragment("0123456789", "42.69", 5);
    Assert.assertEquals("56789", fragment.tail());
    Assert.assertNull(fragment.val_boolean);
    Assert.assertNotNull(fragment.val_double);
    Assert.assertNull(fragment.val_int);
    Assert.assertNull(fragment.val_long);
    Assert.assertTrue((fragment.val_double - 42.69) < 0.01);
  }
}
