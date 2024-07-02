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
  public void to_str() {
    Assert.assertEquals("{}", LibDynamic.to_str(new NtDynamic("{}")));
  }

  @Test
  public void coverage_raw_str() {
    Assert.assertFalse(LibDynamic.str(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.str(new NtDynamic("{}")).has());
    Assert.assertEquals("xyz", LibDynamic.str(LibDynamic.to_dyn("\"xyz\"")).get());
    Assert.assertEquals("123", LibDynamic.str(LibDynamic.to_dyn("123")).get());
    Assert.assertEquals("3.14", LibDynamic.str(LibDynamic.to_dyn("3.14")).get());
    Assert.assertEquals("true", LibDynamic.str(LibDynamic.to_dyn("true")).get());
    Assert.assertEquals("xyz", LibDynamic.str(new NtMaybe<>(LibDynamic.to_dyn("\"xyz\""))).get());
    Assert.assertEquals("123", LibDynamic.str(new NtMaybe<>(LibDynamic.to_dyn("123"))).get());
    Assert.assertEquals("3.14", LibDynamic.str(new NtMaybe<>(LibDynamic.to_dyn("3.14"))).get());
  }

  @Test
  public void coverage_raw_d() {
    Assert.assertFalse(LibDynamic.d(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.d(LibDynamic.to_dyn("\"xyz\"")).has());
    Assert.assertEquals(123, LibDynamic.d(LibDynamic.to_dyn("123")).get(), 0.01);
    Assert.assertEquals(3.14, LibDynamic.d(LibDynamic.to_dyn("3.14")).get(), 0.01);
    Assert.assertEquals(42424242424242.0, LibDynamic.d(LibDynamic.to_dyn("42424242424242")).get(), 0.01);
    Assert.assertFalse(LibDynamic.d(LibDynamic.to_dyn("false")).has());
    Assert.assertFalse(LibDynamic.d(new NtMaybe<>(LibDynamic.to_dyn("\"xyz\""))).has());
    Assert.assertEquals(123, LibDynamic.d(new NtMaybe<>(LibDynamic.to_dyn("123"))).get(), 0.01);
    Assert.assertEquals(3.14, LibDynamic.d(new NtMaybe<>(LibDynamic.to_dyn("3.14"))).get(), 0.01);
    Assert.assertFalse(LibDynamic.d(new NtMaybe<>(LibDynamic.to_dyn("false"))).has());
  }

  @Test
  public void coverage_raw_l() {
    Assert.assertFalse(LibDynamic.l(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.l(LibDynamic.to_dyn("\"xyz\"")).has());
    Assert.assertEquals(123, LibDynamic.l(LibDynamic.to_dyn("123")).get(), 0.01);
    Assert.assertFalse(LibDynamic.l(LibDynamic.to_dyn("3.14")).has());
    Assert.assertFalse(LibDynamic.l(LibDynamic.to_dyn("false")).has());
    Assert.assertFalse(LibDynamic.l(new NtMaybe<>(LibDynamic.to_dyn("\"xyz\""))).has());
    Assert.assertEquals(123, LibDynamic.l(new NtMaybe<>(LibDynamic.to_dyn("123"))).get(), 0.01);
    Assert.assertEquals(42424242424242L, LibDynamic.l(new NtMaybe<>(LibDynamic.to_dyn("42424242424242"))).get(), 0.01);
    Assert.assertFalse(LibDynamic.l(new NtMaybe<>(LibDynamic.to_dyn("3.14"))).has());
    Assert.assertFalse(LibDynamic.l(new NtMaybe<>(LibDynamic.to_dyn("false"))).has());
  }

  @Test
  public void coverage_raw_i() {
    Assert.assertFalse(LibDynamic.i(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.i(LibDynamic.to_dyn("\"xyz\"")).has());
    Assert.assertEquals(123, LibDynamic.i(LibDynamic.to_dyn("123")).get(), 0.01);
    Assert.assertFalse(LibDynamic.i(LibDynamic.to_dyn("3.14")).has());
    Assert.assertFalse(LibDynamic.i(LibDynamic.to_dyn("false")).has());
    Assert.assertFalse(LibDynamic.i(new NtMaybe<>(LibDynamic.to_dyn("\"xyz\""))).has());
    Assert.assertEquals(123, LibDynamic.i(new NtMaybe<>(LibDynamic.to_dyn("123"))).get(), 0.01);
    Assert.assertFalse(LibDynamic.i(new NtMaybe<>(LibDynamic.to_dyn("3.14"))).has());
    Assert.assertFalse(LibDynamic.i(new NtMaybe<>(LibDynamic.to_dyn("false"))).has());
  }

  @Test
  public void coverage_raw_b() {
    Assert.assertFalse(LibDynamic.b(new NtMaybe<>()).has());
    Assert.assertFalse(LibDynamic.b(LibDynamic.to_dyn("\"xyz\"")).has());
    Assert.assertTrue(LibDynamic.b(new NtMaybe<>(LibDynamic.to_dyn("true"))).get());
    Assert.assertFalse(LibDynamic.b(new NtMaybe<>(LibDynamic.to_dyn("false"))).get());
    Assert.assertTrue(LibDynamic.b(new NtMaybe<>(LibDynamic.to_dyn("\"true\""))).get());
    Assert.assertFalse(LibDynamic.b(new NtMaybe<>(LibDynamic.to_dyn("\"false\""))).get());
  }

  @Test
  public void coverage_str() {
    Assert.assertEquals("{}", LibDynamic.to_dyn("{}").get().json);
    Assert.assertFalse(LibDynamic.to_dyn("x").has());
    Assert.assertEquals("here", LibDynamic.str(new NtDynamic("{\"x\":\"here\"}"), "x").get());
    Assert.assertEquals("123", LibDynamic.str(new NtDynamic("{\"x\":123}"), "x").get());
    Assert.assertEquals("123.4", LibDynamic.str(new NtDynamic("{\"x\":123.4}"), "x").get());
    Assert.assertFalse(LibDynamic.str(new NtDynamic("{}"), "x").has());
  }

  @Test
  public void coverage_str_defaults() {
    Assert.assertEquals("{}", LibDynamic.to_dyn("{}").get().json);
    Assert.assertFalse(LibDynamic.to_dyn("x").has());
    Assert.assertEquals("here", LibDynamic.str(new NtDynamic("{\"x\":\"here\"}"), "x", "def"));
    Assert.assertEquals("123", LibDynamic.str(new NtDynamic("{\"x\":123}"), "x", "def"));
    Assert.assertEquals("123.4", LibDynamic.str(new NtDynamic("{\"x\":123.4}"), "x", "def"));
    Assert.assertEquals("def", LibDynamic.str(new NtDynamic("{}"), "x", "def"));
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
  public void coverage_integer_defaults() {
    Assert.assertEquals(123, (int) (LibDynamic.i(new NtDynamic("{\"x\":\"123\"}"), "x", 0)));
    Assert.assertEquals(123, (int) (LibDynamic.i(new NtDynamic("{\"x\":123}"), "x", 0)));
    Assert.assertEquals(42, LibDynamic.i(new NtDynamic("{}"), "x", 42));
    Assert.assertEquals(42, LibDynamic.i(new NtDynamic("{\"x\":\"4.2\"}"), "x", 42));
    Assert.assertEquals(42, LibDynamic.i(new NtDynamic("{\"x\":\"xyz\"}"), "x", 42));
    Assert.assertEquals(42, LibDynamic.i(new NtDynamic("null"), "x", 42));
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
  public void coverage_long_defaults() {
    Assert.assertEquals(123L, (long) (LibDynamic.l(new NtDynamic("{\"x\":\"123\"}"), "x", 0)));
    Assert.assertEquals(123L, (long) (LibDynamic.l(new NtDynamic("{\"x\":123}"), "x", 0)));
    Assert.assertEquals(42424242424242L, (long) (LibDynamic.l(new NtDynamic("{\"x\":42424242424242}"), "x", 0)));
    Assert.assertEquals(42L, LibDynamic.l(new NtDynamic("{}"), "x", 42L));
    Assert.assertEquals(42L, LibDynamic.l(new NtDynamic("{\"x\":\"4.2\"}"), "x", 42L));
    Assert.assertEquals(42L, LibDynamic.l(new NtDynamic("{\"x\":\"xyz\"}"), "x", 42L));
    Assert.assertEquals(42L, LibDynamic.l(new NtDynamic("null"), "x", 42L));
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
  public void coverage_double_defaults() {
    Assert.assertEquals(42.123, (LibDynamic.d(new NtDynamic("{\"x\":\"42.123\"}"), "x", -1.0)), 0.1);
    Assert.assertEquals(42.123, (LibDynamic.d(new NtDynamic("{\"x\":42.123}"), "x", -1.0)), 0.1);
    Assert.assertEquals(42, (LibDynamic.d(new NtDynamic("{\"x\":42}"), "x", -1.0)), 0.1);
    Assert.assertEquals(424242424242.0, (LibDynamic.d(new NtDynamic("{\"x\":424242424242}"), "x", -1.0)), 0.1);
    Assert.assertEquals(-1, LibDynamic.d(new NtDynamic("{}"), "x", -1.0), 0.1);
    Assert.assertEquals(-1, LibDynamic.d(new NtDynamic("{\"x\":\"str\"}"), "x", -1.0), 0.1);
    Assert.assertEquals(-1, LibDynamic.d(new NtDynamic("null"), "x", -1.0), 0.1);
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
  public void coverage_boolean_defaults() {
    Assert.assertTrue((LibDynamic.b(new NtDynamic("{\"x\":\"true\"}"), "x", true)));
    Assert.assertFalse((LibDynamic.b(new NtDynamic("{\"x\":\"false\"}"), "x", true)));
    Assert.assertTrue((LibDynamic.b(new NtDynamic("{\"x\":\"true\"}"), "x", false)));
    Assert.assertFalse((LibDynamic.b(new NtDynamic("{\"x\":\"false\"}"), "x", false)));
    Assert.assertTrue((LibDynamic.b(new NtDynamic("{\"x\":true}"), "x", false)));
    Assert.assertFalse((LibDynamic.b(new NtDynamic("{\"x\":false}"), "x", false)));
    Assert.assertTrue((LibDynamic.b(new NtDynamic("{\"x\":true}"), "x", true)));
    Assert.assertFalse((LibDynamic.b(new NtDynamic("{\"x\":false}"), "x", true)));
    Assert.assertTrue(LibDynamic.b(new NtDynamic("{\"x\":\"4.2\"}"), "x", true));
    Assert.assertFalse(LibDynamic.b(new NtDynamic("{\"x\":\"4.2\"}"), "x", false));
    Assert.assertTrue(LibDynamic.b(new NtDynamic("{\"x\":4.2}"), "x", true));
    Assert.assertFalse(LibDynamic.b(new NtDynamic("{\"x\":4.2}"), "x", false));
    Assert.assertTrue(LibDynamic.b(new NtDynamic("null"), "x", true));
    Assert.assertFalse(LibDynamic.b(new NtDynamic("null"), "x", false));
  }

  @Test
  public void coverage_is_null() {
    Assert.assertFalse((LibDynamic.is_null(new NtDynamic("{\"x\":\"true\"}"), "x").get()));
    Assert.assertTrue((LibDynamic.is_null(new NtDynamic("{\"x\":null}"), "x").get()));
    Assert.assertFalse((LibDynamic.is_null(new NtDynamic("{}"), "x").has()));
    Assert.assertFalse(LibDynamic.is_null(new NtDynamic("null"), "x").has());
  }

  @Test
  public void coverage_date() {
    Assert.assertEquals("2000-07-01", LibDynamic.date(new NtDynamic("\"2000/7/1\"")).get().toString());
    Assert.assertEquals("2000-07-01", LibDynamic.date(new NtDynamic("{\"x\":\"2000/7/1\"}"), "x").get().toString());
    Assert.assertEquals("2000-07-01", LibDynamic.date(new NtDynamic("{\"x\":\"2000/7/1\"}"), "x", "-").toString());
  }

  @Test
  public void coverage_datetime() {
    Assert.assertEquals("2023-04-24T17:57:19.802528800-05:00[America/Chicago]", LibDynamic.datetime(new NtDynamic("\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"")).get().toString());
    Assert.assertEquals("2023-04-24T17:57:19.802528800-05:00[America/Chicago]", LibDynamic.datetime(new NtDynamic("{\"x\":\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"}"), "x").get().toString());
    Assert.assertEquals("2023-04-24T17:57:19.802528800-05:00[America/Chicago]", LibDynamic.datetime(new NtDynamic("{\"x\":\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"}"), "x", "-").toString());
  }

  @Test
  public void parse() {
    Assert.assertTrue(LibDynamic.parse("{}").has());
    Assert.assertFalse(LibDynamic.parse("xyz").has());
  }
}
