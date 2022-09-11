/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtDynamic;
import org.junit.Assert;
import org.junit.Test;

public class LibDynamicTests {
  @Test
  public void coverage_str() {
    Assert.assertEquals("{}", LibDynamic.toDynamic("{}").get().json);
    Assert.assertFalse(LibDynamic.toDynamic("x").has());
    Assert.assertEquals("here", LibDynamic.str(new NtDynamic("{\"x\":\"here\"}"), "x").get());
    Assert.assertEquals("123", LibDynamic.str(new NtDynamic("{\"x\":123}"), "x").get());
    Assert.assertEquals("123.4", LibDynamic.str(new NtDynamic("{\"x\":123.4}"), "x").get());
    Assert.assertFalse(LibDynamic.str(new NtDynamic("{}"), "x").has());
  }

  @Test
  public void coverage_integer() {
    Assert.assertEquals(123, (int) (LibDynamic.i(new NtDynamic("{\"x\":\"123\"}"), "x").get()));
    Assert.assertEquals(123, (int) (LibDynamic.i(new NtDynamic("{\"x\":123}"), "x").get()));
    Assert.assertFalse(LibDynamic.i(new NtDynamic("{}"), "x").has());
    Assert.assertFalse(LibDynamic.i(new NtDynamic("{\"x\":\"4.2\"}"), "x").has());
    Assert.assertFalse(LibDynamic.i(new NtDynamic("{\"x\":\"xyz\"}"), "x").has());
    Assert.assertFalse(LibDynamic.i(new NtDynamic("null"), "x").has());
  }

  @Test
  public void coverage_long() {
    Assert.assertEquals(123L, (long) (LibDynamic.l(new NtDynamic("{\"x\":\"123\"}"), "x").get()));
    Assert.assertEquals(123L, (long) (LibDynamic.l(new NtDynamic("{\"x\":123}"), "x").get()));
    Assert.assertFalse(LibDynamic.l(new NtDynamic("{}"), "x").has());
    Assert.assertFalse(LibDynamic.l(new NtDynamic("{\"x\":\"4.2\"}"), "x").has());
    Assert.assertFalse(LibDynamic.l(new NtDynamic("{\"x\":\"xyz\"}"), "x").has());
    Assert.assertFalse(LibDynamic.l(new NtDynamic("null"), "x").has());
  }

  @Test
  public void coverage_double() {
    Assert.assertEquals(42.123, (LibDynamic.d(new NtDynamic("{\"x\":\"42.123\"}"), "x").get()), 0.1);
    Assert.assertEquals(42.123, (LibDynamic.d(new NtDynamic("{\"x\":42.123}"), "x").get()), 0.1);
    Assert.assertEquals(42, (LibDynamic.d(new NtDynamic("{\"x\":42}"), "x").get()), 0.1);
    Assert.assertFalse(LibDynamic.d(new NtDynamic("{}"), "x").has());
    Assert.assertFalse(LibDynamic.d(new NtDynamic("{\"x\":\"str\"}"), "x").has());
    Assert.assertFalse(LibDynamic.d(new NtDynamic("null"), "x").has());
  }

  @Test
  public void coverage_boolean() {
    Assert.assertTrue((LibDynamic.b(new NtDynamic("{\"x\":\"true\"}"), "x").get()));
    Assert.assertFalse((LibDynamic.b(new NtDynamic("{\"x\":\"false\"}"), "x").get()));
    Assert.assertTrue((LibDynamic.b(new NtDynamic("{\"x\":true}"), "x").get()));
    Assert.assertFalse((LibDynamic.b(new NtDynamic("{\"x\":false}"), "x").get()));
    Assert.assertFalse(LibDynamic.b(new NtDynamic("{\"x\":\"4.2\"}"), "x").has());
    Assert.assertFalse(LibDynamic.b(new NtDynamic("{\"x\":4.2}"), "x").has());
    Assert.assertFalse(LibDynamic.b(new NtDynamic("null"), "x").has());
  }

  @Test
  public void coverage_is_null() {
    Assert.assertFalse((LibDynamic.is_null(new NtDynamic("{\"x\":\"true\"}"), "x").get()));
    Assert.assertTrue((LibDynamic.is_null(new NtDynamic("{\"x\":null}"), "x").get()));
    Assert.assertFalse((LibDynamic.is_null(new NtDynamic("{}"), "x").has()));
    Assert.assertFalse(LibDynamic.is_null(new NtDynamic("null"), "x").has());
  }
}
