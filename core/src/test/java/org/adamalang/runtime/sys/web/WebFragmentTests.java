/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
