/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class LibDynamicTests {

  @Test
  public void coverage_dyn() {
    Assert.assertEquals("123", LibDynamic.dyn(123).json);
    Assert.assertEquals("3.14", LibDynamic.dyn(3.14).json);
    Assert.assertEquals("123", LibDynamic.dyn(123L).json);
    Assert.assertEquals("true", LibDynamic.dyn(true).json);
    Assert.assertEquals("\"xyz\"", LibDynamic.dyn("xyz").json);
  }

  @Test
  public void coverage_raw_str() {
    Assert.assertFalse(LibDynamic.str(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.str(new NtDynamic("{}")).has());
    Assert.assertEquals("xyz", LibDynamic.str(LibDynamic.toDynamic("\"xyz\"")).get());
    Assert.assertEquals("123", LibDynamic.str(LibDynamic.toDynamic("123")).get());
    Assert.assertEquals("3.14", LibDynamic.str(LibDynamic.toDynamic("3.14")).get());
    Assert.assertEquals("true", LibDynamic.str(LibDynamic.toDynamic("true")).get());
    Assert.assertEquals("xyz", LibDynamic.str(new NtMaybe<>(LibDynamic.toDynamic("\"xyz\""))).get());
    Assert.assertEquals("123", LibDynamic.str(new NtMaybe<>(LibDynamic.toDynamic("123"))).get());
    Assert.assertEquals("3.14", LibDynamic.str(new NtMaybe<>(LibDynamic.toDynamic("3.14"))).get());
  }

  @Test
  public void coverage_raw_d() {
    Assert.assertFalse(LibDynamic.d(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.d(LibDynamic.toDynamic("\"xyz\"")).has());
    Assert.assertEquals(123, LibDynamic.d(LibDynamic.toDynamic("123")).get(), 0.01);
    Assert.assertEquals(3.14, LibDynamic.d(LibDynamic.toDynamic("3.14")).get(), 0.01);
    Assert.assertEquals(42424242424242.0, LibDynamic.d(LibDynamic.toDynamic("42424242424242")).get(), 0.01);
    Assert.assertFalse(LibDynamic.d(LibDynamic.toDynamic("false")).has());
    Assert.assertFalse(LibDynamic.d(new NtMaybe<>(LibDynamic.toDynamic("\"xyz\""))).has());
    Assert.assertEquals(123, LibDynamic.d(new NtMaybe<>(LibDynamic.toDynamic("123"))).get(), 0.01);
    Assert.assertEquals(3.14, LibDynamic.d(new NtMaybe<>(LibDynamic.toDynamic("3.14"))).get(), 0.01);
    Assert.assertFalse(LibDynamic.d(new NtMaybe<>(LibDynamic.toDynamic("false"))).has());
  }

  @Test
  public void coverage_raw_l() {
    Assert.assertFalse(LibDynamic.l(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.l(LibDynamic.toDynamic("\"xyz\"")).has());
    Assert.assertEquals(123, LibDynamic.l(LibDynamic.toDynamic("123")).get(), 0.01);
    Assert.assertFalse(LibDynamic.l(LibDynamic.toDynamic("3.14")).has());
    Assert.assertFalse(LibDynamic.l(LibDynamic.toDynamic("false")).has());
    Assert.assertFalse(LibDynamic.l(new NtMaybe<>(LibDynamic.toDynamic("\"xyz\""))).has());
    Assert.assertEquals(123, LibDynamic.l(new NtMaybe<>(LibDynamic.toDynamic("123"))).get(), 0.01);
    Assert.assertEquals(42424242424242L, LibDynamic.l(new NtMaybe<>(LibDynamic.toDynamic("42424242424242"))).get(), 0.01);
    Assert.assertFalse(LibDynamic.l(new NtMaybe<>(LibDynamic.toDynamic("3.14"))).has());
    Assert.assertFalse(LibDynamic.l(new NtMaybe<>(LibDynamic.toDynamic("false"))).has());
  }

  @Test
  public void coverage_raw_i() {
    Assert.assertFalse(LibDynamic.i(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.i(LibDynamic.toDynamic("\"xyz\"")).has());
    Assert.assertEquals(123, LibDynamic.i(LibDynamic.toDynamic("123")).get(), 0.01);
    Assert.assertFalse(LibDynamic.i(LibDynamic.toDynamic("3.14")).has());
    Assert.assertFalse(LibDynamic.i(LibDynamic.toDynamic("false")).has());
    Assert.assertFalse(LibDynamic.i(new NtMaybe<>(LibDynamic.toDynamic("\"xyz\""))).has());
    Assert.assertEquals(123, LibDynamic.i(new NtMaybe<>(LibDynamic.toDynamic("123"))).get(), 0.01);
    Assert.assertFalse(LibDynamic.i(new NtMaybe<>(LibDynamic.toDynamic("3.14"))).has());
    Assert.assertFalse(LibDynamic.i(new NtMaybe<>(LibDynamic.toDynamic("false"))).has());
  }

  @Test
  public void coverage_raw_b() {
    Assert.assertFalse(LibDynamic.b(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.b(LibDynamic.toDynamic("\"xyz\"")).has());
    Assert.assertTrue(LibDynamic.b(new NtMaybe<>(LibDynamic.toDynamic("true"))).get());
    Assert.assertFalse(LibDynamic.b(new NtMaybe<>(LibDynamic.toDynamic("false"))).get());
    Assert.assertTrue(LibDynamic.b(new NtMaybe<>(LibDynamic.toDynamic("\"true\""))).get());
    Assert.assertFalse(LibDynamic.b(new NtMaybe<>(LibDynamic.toDynamic("\"false\""))).get());
  }

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
    Assert.assertEquals(42424242424242L, (long) (LibDynamic.l(new NtDynamic("{\"x\":42424242424242}"), "x").get()));
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
    Assert.assertEquals(424242424242.0, (LibDynamic.d(new NtDynamic("{\"x\":424242424242}"), "x").get()), 0.1);
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
