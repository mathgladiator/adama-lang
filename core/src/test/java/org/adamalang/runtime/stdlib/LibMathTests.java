/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class LibMathTests {

  @Test
  public void intersects() {
    Assert.assertTrue(LibMath.intersects(1, 4, 2, 3));
    Assert.assertFalse(LibMath.intersects(1, 4, 5, 10));
    Assert.assertTrue(LibMath.intersects(1.0, 4.0, 2.0, 3.0));
    Assert.assertFalse(LibMath.intersects(1.0, 4.0, 5.0, 10.0));
    Assert.assertTrue(LibMath.intersects(1L, 4L, 2L, 3L));
    Assert.assertFalse(LibMath.intersects(1L, 4L, 5L, 10L));
  }

  @Test
  public void valid() {
    Assert.assertTrue(LibMath.isInfinite(Double.POSITIVE_INFINITY));
    Assert.assertTrue(LibMath.isInfinite(new NtMaybe<>(Double.POSITIVE_INFINITY)));
    Assert.assertFalse(LibMath.isInfinite(new NtMaybe<>(1.2)));
    Assert.assertTrue(LibMath.isNaN(Double.NaN));
    Assert.assertTrue(LibMath.isNaN(new NtMaybe<>(Double.NaN)));
    Assert.assertTrue(LibMath.isNaN(new NtMaybe<>()));
    Assert.assertEquals(1.0, LibMath.valid(1.0).get(), 0.01);
    Assert.assertFalse(LibMath.valid(Double.NaN).has());
    Assert.assertFalse(LibMath.valid(Double.POSITIVE_INFINITY).has());
    Assert.assertEquals(1.0, LibMath.valid(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.valid(new NtMaybe<>(Double.NaN)).has());
    Assert.assertFalse(LibMath.valid(new NtMaybe<>(Double.POSITIVE_INFINITY)).has());
  }

  @Test
  public void trig() {
    Assert.assertEquals(0.03490658503988659, LibMath.radians(2.0), 0.01);
    Assert.assertEquals(0.03490658503988659, LibMath.radians(new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibMath.radians(new NtMaybe<>()).has());
    Assert.assertEquals(2.0, LibMath.degrees(0.03490658503988659), 0.01);
    Assert.assertEquals(2.0, LibMath.degrees(new NtMaybe<>(0.03490658503988659)).get(), 0.01);
    Assert.assertFalse(LibMath.degrees(new NtMaybe<>()).has());
  }

  @Test
  public void basics() {
    Assert.assertTrue(LibMath.near(1, LibMath.floor(1.6)));
    Assert.assertTrue(LibMath.near(2, LibMath.ceil(1.6)));
    Assert.assertTrue(LibMath.near(1, LibMath.floor(1.6, 1.0)));
    Assert.assertTrue(LibMath.near(2, LibMath.ceil(1.6, 1.0)));
    Assert.assertTrue(LibMath.near(1.6, LibMath.floor(1.67, 0.1)));
    Assert.assertTrue(LibMath.near(1.7, LibMath.ceil(1.67, 0.1)));


    Assert.assertTrue(LibMath.near(1, LibMath.floor(new NtMaybe<>(1.6)).get()));
    Assert.assertTrue(LibMath.near(2, LibMath.ceil(new NtMaybe<>(1.6)).get()));
    Assert.assertTrue(LibMath.near(1, LibMath.floor(new NtMaybe<>(1.6), 1.0).get()));
    Assert.assertTrue(LibMath.near(2, LibMath.ceil(new NtMaybe<>(1.6), 1.0).get()));
    Assert.assertTrue(LibMath.near(1.6, LibMath.floor(new NtMaybe<>(1.67), 0.1).get()));
    Assert.assertTrue(LibMath.near(1.7, LibMath.ceil(new NtMaybe<>(1.67), 0.1).get()));

    Assert.assertFalse(LibMath.floor(new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.ceil(new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floor(new NtMaybe<>(), 1.0).has());
    Assert.assertFalse(LibMath.ceil(new NtMaybe<>(), 1.0).has());

    Assert.assertTrue(LibMath.near(2, LibMath.round(1.6)));
    Assert.assertTrue(LibMath.near(1, LibMath.round(1.4)));
    Assert.assertTrue(LibMath.near(1.7, LibMath.round(1.66, 0.1)));
    Assert.assertTrue(LibMath.near(1.6, LibMath.round(1.64, 0.1)));
    Assert.assertTrue(LibMath.near(0.3, 0.2 + 0.1));
    Assert.assertTrue(LibMath.near(1.4142135623730950488016887242097, LibMath.SQRT2));
    Assert.assertTrue(LibMath.near(0.3382, LibMath.roundTo(0.338198742, 4)));
    Assert.assertTrue(LibMath.near(0.338, LibMath.roundTo(0.338198742, 3)));
    Assert.assertTrue(LibMath.near(0.34, LibMath.roundTo(0.338198742, 2)));
    Assert.assertTrue(LibMath.near(0.3, LibMath.roundTo(0.338198742, 1)));
    Assert.assertTrue(LibMath.near(0.0, LibMath.roundTo(0.338198742, 0)));
  }

  @Test
  public void absolute_value() {
    Assert.assertEquals(4, LibMath.abs(-4));
    Assert.assertEquals(4.3, LibMath.abs(-4.3), 0.001);
    Assert.assertEquals(4L, LibMath.abs(-4L));
    Assert.assertTrue(LibMath.near(2, LibMath.abs_d(new NtMaybe<>(-2.0)).get()));
    Assert.assertFalse(LibMath.abs_d(new NtMaybe<>()).has());
    Assert.assertEquals(2, (int) LibMath.abs_i(new NtMaybe<>(-2)).get());
    Assert.assertFalse(LibMath.abs_i(new NtMaybe<>()).has());
    Assert.assertEquals(2, (long) LibMath.abs_l(new NtMaybe<>(-2L)).get());
    Assert.assertFalse(LibMath.abs_l(new NtMaybe<>()).has());
  }

  @Test
  public void complex() {
    NtComplex a = new NtComplex(1, 2);
    Assert.assertTrue(LibMath.near(2.23606797749979, LibMath.abs(a)));
    Assert.assertTrue(LibMath.near(2.23606797749979, LibMath.abs_c(new NtMaybe<>(a)).get()));
    Assert.assertTrue(LibMath.near(new NtComplex(1, -2), LibMath.conj(a)));
    Assert.assertTrue(LibMath.near(new NtComplex(1, -2), LibMath.conj(new NtMaybe<>(a)).get()));
    Assert.assertFalse(LibMath.conj(new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.length(new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.abs_c(new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.abs_c(new NtMaybe<>()).has());
    NtComplex t = new NtComplex(1, 0.0);
    Assert.assertTrue(LibMath.near(t, 1.0));
    Assert.assertTrue(LibMath.near(t, 1L));
    Assert.assertTrue(LibMath.near(t, 1));
    Assert.assertTrue(LibMath.near(t, t));
    Assert.assertTrue(LibMath.near(1.0, LibMath.abs_c(new NtMaybe<>(t)).get()));
    Assert.assertTrue(LibMath.near(5, LibMath.abs_c(new NtMaybe<>(new NtComplex(3, 4))).get()));
  }

  @Test
  public void sqrt() {
    {
      NtComplex b = LibMath.sqrt(-4);
      Assert.assertTrue(LibMath.near(b.imaginary, 2));
      b = LibMath.sqrt(4);
      Assert.assertTrue(LibMath.near(b.real, 2));
    }

    Assert.assertFalse(LibMath.sqrt(new NtMaybe<>()).has());
    {
      NtComplex b = LibMath.sqrt(new NtMaybe<Double>(-4.0)).get();
      Assert.assertTrue(LibMath.near(b.imaginary, 2));
      b = LibMath.sqrt(new NtMaybe<Double>(4.0)).get();
      Assert.assertTrue(LibMath.near(b.real, 2));
    }
  }

  @Test
  public void xor() {
    Assert.assertFalse(LibMath.xor(true, true));
    Assert.assertTrue(LibMath.xor(true, false));
    Assert.assertTrue(LibMath.xor(false, true));
    Assert.assertFalse(LibMath.xor(false, false));
  }

  @Test
  public void truth() {
    Assert.assertTrue(LibMath.isTrue(new NtMaybe<>(true)));
    Assert.assertFalse(LibMath.isTrue(new NtMaybe<>(false)));
    Assert.assertFalse(LibMath.isTrue(new NtMaybe<>()));
  }

  @Test
  public void rounding() {
    Assert.assertEquals(1.5, LibMath.round(1.49, 0.1), 0.001);
    Assert.assertEquals(1, LibMath.round(1.49), 0.001);
    Assert.assertEquals(1.5, LibMath.round(new NtMaybe<>(1.49), 0.1).get(), 0.001);
    Assert.assertEquals(1, LibMath.round(new NtMaybe<>(1.49)).get(), 0.001);
    Assert.assertFalse(LibMath.round(new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.round(new NtMaybe<>(), 0.01).has());
    Assert.assertEquals(1.5, LibMath.roundTo(new NtMaybe<>(1.49), 1).get(), 0.001);
    Assert.assertEquals(1, LibMath.roundTo(new NtMaybe<>(1.49), 0).get(), 0.001);
    Assert.assertFalse(LibMath.roundTo(new NtMaybe<>(), 1).has());
  }

  @Test
  public void equality() {
    Assert.assertFalse(LibMath.equality(new NtMaybe<String>(), "X", (x, y) -> x.equals(y)));
    Assert.assertTrue(LibMath.equality(new NtMaybe<String>("X"), "X", (x, y) -> x.equals(y)));
  }

  @Test
  public void test_sin() {
    Assert.assertEquals(0.8414709848078965, LibMath.sin(1.0), 0.01);
    Assert.assertEquals(0.8414709848078965, LibMath.sin(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.sin(new NtMaybe<>()).has());
  }

  @Test
  public void test_cos() {
    Assert.assertEquals(0.5403023058681398, LibMath.cos(1.0), 0.01);
    Assert.assertEquals(0.5403023058681398, LibMath.cos(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.cos(new NtMaybe<>()).has());
  }

  @Test
  public void test_tan() {
    Assert.assertEquals(1.5574077246549023, LibMath.tan(1.0), 0.01);
    Assert.assertEquals(1.5574077246549023, LibMath.tan(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.tan(new NtMaybe<>()).has());
  }

  @Test
  public void test_asin() {
    Assert.assertEquals(1.5707963267948966, LibMath.asin(1.0), 0.01);
    Assert.assertEquals(1.5707963267948966, LibMath.asin(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.asin(new NtMaybe<>()).has());
  }

  @Test
  public void test_acos() {
    Assert.assertEquals(0.0, LibMath.acos(1.0), 0.01);
    Assert.assertEquals(0.0, LibMath.acos(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.acos(new NtMaybe<>()).has());
  }

  @Test
  public void test_atan() {
    Assert.assertEquals(0.7853981633974483, LibMath.atan(1.0), 0.01);
    Assert.assertEquals(0.7853981633974483, LibMath.atan(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.atan(new NtMaybe<>()).has());
  }

  @Test
  public void test_sinh() {
    Assert.assertEquals(1.1752011936438014, LibMath.sinh(1.0), 0.01);
    Assert.assertEquals(1.1752011936438014, LibMath.sinh(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.sinh(new NtMaybe<>()).has());
  }

  @Test
  public void test_cosh() {
    Assert.assertEquals(1.543080634815244, LibMath.cosh(1.0), 0.01);
    Assert.assertEquals(1.543080634815244, LibMath.cosh(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.cosh(new NtMaybe<>()).has());
  }

  @Test
  public void test_tanh() {
    Assert.assertEquals(0.7615941559557649, LibMath.tanh(1.0), 0.01);
    Assert.assertEquals(0.7615941559557649, LibMath.tanh(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.tanh(new NtMaybe<>()).has());
  }

  @Test
  public void test_exp() {
    Assert.assertEquals(2.718281828459045, LibMath.exp(1.0), 0.01);
    Assert.assertEquals(2.718281828459045, LibMath.exp(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.exp(new NtMaybe<>()).has());
  }

  @Test
  public void test_log() {
    Assert.assertEquals(0.0, LibMath.log(1.0), 0.01);
    Assert.assertEquals(0.0, LibMath.log(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.log(new NtMaybe<>()).has());
  }

  @Test
  public void test_log10() {
    Assert.assertEquals(0.0, LibMath.log10(1.0), 0.01);
    Assert.assertEquals(0.0, LibMath.log10(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.log10(new NtMaybe<>()).has());
  }

  @Test
  public void test_cbrt() {
    Assert.assertEquals(1.0, LibMath.cbrt(1.0), 0.01);
    Assert.assertEquals(1.0, LibMath.cbrt(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.cbrt(new NtMaybe<>()).has());
  }

  @Test
  public void test_expm1() {
    Assert.assertEquals(1.718281828459045, LibMath.expm1(1.0), 0.01);
    Assert.assertEquals(1.718281828459045, LibMath.expm1(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.expm1(new NtMaybe<>()).has());
  }

  @Test
  public void test_log1p() {
    Assert.assertEquals(0.6931471805599453, LibMath.log1p(1.0), 0.01);
    Assert.assertEquals(0.6931471805599453, LibMath.log1p(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.log1p(new NtMaybe<>()).has());
  }

  @Test
  public void test_signum() {
    Assert.assertEquals(1.0, LibMath.signum(1.0), 0.01);
    Assert.assertEquals(1.0, LibMath.signum(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.signum(new NtMaybe<>()).has());
  }

  @Test
  public void test_ulp() {
    Assert.assertEquals(2.220446049250313E-16, LibMath.ulp(1.0), 0.01);
    Assert.assertEquals(2.220446049250313E-16, LibMath.ulp(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.ulp(new NtMaybe<>()).has());
  }

  @Test
  public void test_getExponent() {
    Assert.assertEquals(0, LibMath.getExponent(1.0), 0.01);
    Assert.assertEquals(0, LibMath.getExponent(new NtMaybe<>(1.0)).get(), 0.01);
    Assert.assertFalse(LibMath.getExponent(new NtMaybe<>()).has());
  }

  @Test
  public void test_min() {
    Assert.assertEquals(1.0, LibMath.min(1.0, 2.0), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.min(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.min(new NtMaybe<>(1.0), 2.0).get(), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.min(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibMath.min(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.min(new NtMaybe<>(), new NtMaybe<>(2.0)).has());
    Assert.assertFalse(LibMath.min(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.min(new NtMaybe<>(), 2.0).has());
    Assert.assertFalse(LibMath.min(1.0, new NtMaybe<>()).has());
  }

  @Test
  public void test_max() {
    Assert.assertEquals(2.0, LibMath.max(1.0, 2.0), 0.01);
    Assert.assertEquals(2.0, (double) LibMath.max(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(2.0, (double) LibMath.max(new NtMaybe<>(1.0), 2.0).get(), 0.01);
    Assert.assertEquals(2.0, (double) LibMath.max(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibMath.max(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.max(new NtMaybe<>(), new NtMaybe<>(2.0)).has());
    Assert.assertFalse(LibMath.max(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.max(new NtMaybe<>(), 2.0).has());
    Assert.assertFalse(LibMath.max(1.0, new NtMaybe<>()).has());
  }

  @Test
  public void test_atan2() {
    Assert.assertEquals(0.4636476090008061, LibMath.atan2(1.0, 2.0), 0.01);
    Assert.assertEquals(0.4636476090008061, (double) LibMath.atan2(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(0.4636476090008061, (double) LibMath.atan2(new NtMaybe<>(1.0), 2.0).get(), 0.01);
    Assert.assertEquals(0.4636476090008061, (double) LibMath.atan2(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibMath.atan2(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.atan2(new NtMaybe<>(), new NtMaybe<>(2.0)).has());
    Assert.assertFalse(LibMath.atan2(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.atan2(new NtMaybe<>(), 2.0).has());
    Assert.assertFalse(LibMath.atan2(1.0, new NtMaybe<>()).has());
  }

  @Test
  public void test_hypot() {
    Assert.assertEquals(2.23606797749979, LibMath.hypot(1.0, 2.0), 0.01);
    Assert.assertEquals(2.23606797749979, (double) LibMath.hypot(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(2.23606797749979, (double) LibMath.hypot(new NtMaybe<>(1.0), 2.0).get(), 0.01);
    Assert.assertEquals(2.23606797749979, (double) LibMath.hypot(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibMath.hypot(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.hypot(new NtMaybe<>(), new NtMaybe<>(2.0)).has());
    Assert.assertFalse(LibMath.hypot(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.hypot(new NtMaybe<>(), 2.0).has());
    Assert.assertFalse(LibMath.hypot(1.0, new NtMaybe<>()).has());
  }

  @Test
  public void test_pow() {
    Assert.assertEquals(1.0, LibMath.pow(1.0, 2.0), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.pow(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.pow(new NtMaybe<>(1.0), 2.0).get(), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.pow(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibMath.pow(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.pow(new NtMaybe<>(), new NtMaybe<>(2.0)).has());
    Assert.assertFalse(LibMath.pow(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.pow(new NtMaybe<>(), 2.0).has());
    Assert.assertFalse(LibMath.pow(1.0, new NtMaybe<>()).has());
  }

  @Test
  public void test_IEEEremainder() {
    Assert.assertEquals(1.0, LibMath.IEEEremainder(1.0, 2.0), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.IEEEremainder(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.IEEEremainder(new NtMaybe<>(1.0), 2.0).get(), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.IEEEremainder(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibMath.IEEEremainder(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.IEEEremainder(new NtMaybe<>(), new NtMaybe<>(2.0)).has());
    Assert.assertFalse(LibMath.IEEEremainder(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.IEEEremainder(new NtMaybe<>(), 2.0).has());
    Assert.assertFalse(LibMath.IEEEremainder(1.0, new NtMaybe<>()).has());
  }

  @Test
  public void test_copySign() {
    Assert.assertEquals(1.0, LibMath.copySign(1.0, 2.0), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.copySign(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.copySign(new NtMaybe<>(1.0), 2.0).get(), 0.01);
    Assert.assertEquals(1.0, (double) LibMath.copySign(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibMath.copySign(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.copySign(new NtMaybe<>(), new NtMaybe<>(2.0)).has());
    Assert.assertFalse(LibMath.copySign(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.copySign(new NtMaybe<>(), 2.0).has());
    Assert.assertFalse(LibMath.copySign(1.0, new NtMaybe<>()).has());
  }

  /** min */
  @Test
  public void test_min_i() {
    Assert.assertEquals(1, LibMath.min(1, 2));
    Assert.assertEquals(1, (int) LibMath.min_i(1, new NtMaybe<>(2)).get());
    Assert.assertEquals(1, (int) LibMath.min_i(new NtMaybe<>(1), 2).get());
    Assert.assertEquals(1, (int) LibMath.min_i(new NtMaybe<>(1), new NtMaybe<>(2)).get());
    Assert.assertFalse(LibMath.min_i(new NtMaybe<>(1), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.min_i(new NtMaybe<>(), new NtMaybe<>(2)).has());
    Assert.assertFalse(LibMath.min_i(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.min_i(new NtMaybe<>(), 2).has());
    Assert.assertFalse(LibMath.min_i(1, new NtMaybe<>()).has());
  }

  /** max */
  @Test
  public void test_max_i() {
    Assert.assertEquals(2, LibMath.max(1, 2));
    Assert.assertEquals(2, (int) LibMath.max_i(1, new NtMaybe<>(2)).get());
    Assert.assertEquals(2, (int) LibMath.max_i(new NtMaybe<>(1), 2).get());
    Assert.assertEquals(2, (int) LibMath.max_i(new NtMaybe<>(1), new NtMaybe<>(2)).get());
    Assert.assertFalse(LibMath.max_i(new NtMaybe<>(1), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.max_i(new NtMaybe<>(), new NtMaybe<>(2)).has());
    Assert.assertFalse(LibMath.max_i(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.max_i(new NtMaybe<>(), 2).has());
    Assert.assertFalse(LibMath.max_i(1, new NtMaybe<>()).has());
  }

  /** floorDiv */
  @Test
  public void test_floorDiv_i() {
    Assert.assertEquals(0, LibMath.floorDiv(1, 2));
    Assert.assertEquals(0, (int) LibMath.floorDiv_i(1, new NtMaybe<>(2)).get());
    Assert.assertEquals(0, (int) LibMath.floorDiv_i(new NtMaybe<>(1), 2).get());
    Assert.assertEquals(0, (int) LibMath.floorDiv_i(new NtMaybe<>(1), new NtMaybe<>(2)).get());
    Assert.assertFalse(LibMath.floorDiv_i(new NtMaybe<>(1), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floorDiv_i(new NtMaybe<>(), new NtMaybe<>(2)).has());
    Assert.assertFalse(LibMath.floorDiv_i(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floorDiv_i(new NtMaybe<>(), 2).has());
    Assert.assertFalse(LibMath.floorDiv_i(1, new NtMaybe<>()).has());

  }

  /** floorMod */
  @Test
  public void test_floorMod_i() {
    Assert.assertEquals(1, LibMath.floorMod(1, 2));
    Assert.assertEquals(1, (int) LibMath.floorMod_i(1, new NtMaybe<>(2)).get());
    Assert.assertEquals(1, (int) LibMath.floorMod_i(new NtMaybe<>(1), 2).get());
    Assert.assertEquals(1, (int) LibMath.floorMod_i(new NtMaybe<>(1), new NtMaybe<>(2)).get());
    Assert.assertFalse(LibMath.floorMod_i(new NtMaybe<>(1), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floorMod_i(new NtMaybe<>(), new NtMaybe<>(2)).has());
    Assert.assertFalse(LibMath.floorMod_i(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floorMod_i(new NtMaybe<>(), 2).has());
    Assert.assertFalse(LibMath.floorMod_i(1, new NtMaybe<>()).has());
  }

  /** min */
  @Test
  public void test_min_l() {
    Assert.assertEquals(1L, LibMath.min(1L, 2L));
    Assert.assertEquals(1L, (long) LibMath.min_l(1L, new NtMaybe<>(2L)).get());
    Assert.assertEquals(1L, (long) LibMath.min_l(new NtMaybe<>(1L), 2L).get());
    Assert.assertEquals(1L, (long) LibMath.min_l(new NtMaybe<>(1L), new NtMaybe<>(2L)).get());
    Assert.assertFalse(LibMath.min_l(new NtMaybe<>(1L), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.min_l(new NtMaybe<>(), 2L).has());
    Assert.assertFalse(LibMath.min_l(1L, new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.min_l(new NtMaybe<>(), new NtMaybe<>(2L)).has());
    Assert.assertFalse(LibMath.min_l(new NtMaybe<>(), new NtMaybe<>()).has());
  }

  /** max */
  @Test
  public void test_max_l() {
    Assert.assertEquals(2L, LibMath.max(1L, 2L));
    Assert.assertEquals(2L, (long) LibMath.max_l(1L, new NtMaybe<>(2L)).get());
    Assert.assertEquals(2L, (long) LibMath.max_l(new NtMaybe<>(1L), 2L).get());
    Assert.assertEquals(2L, (long) LibMath.max_l(new NtMaybe<>(1L), new NtMaybe<>(2L)).get());
    Assert.assertFalse(LibMath.max_l(new NtMaybe<>(1L), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.max_l(new NtMaybe<>(), 2L).has());
    Assert.assertFalse(LibMath.max_l(1L, new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.max_l(new NtMaybe<>(), new NtMaybe<>(2L)).has());
    Assert.assertFalse(LibMath.max_l(new NtMaybe<>(), new NtMaybe<>()).has());
  }

  /** floorDiv */
  @Test
  public void test_floorDiv_l() {
    Assert.assertEquals(0L, LibMath.floorDiv(1L, 2L));
    Assert.assertEquals(0L, (long) LibMath.floorDiv_l(1L, new NtMaybe<>(2L)).get());
    Assert.assertEquals(0L, (long) LibMath.floorDiv_l(new NtMaybe<>(1L), 2L).get());
    Assert.assertEquals(0L, (long) LibMath.floorDiv_l(new NtMaybe<>(1L), new NtMaybe<>(2L)).get());
    Assert.assertFalse(LibMath.floorDiv_l(new NtMaybe<>(1L), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floorDiv_l(new NtMaybe<>(), 2L).has());
    Assert.assertFalse(LibMath.floorDiv_l(1L, new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floorDiv_l(new NtMaybe<>(), new NtMaybe<>(2L)).has());
    Assert.assertFalse(LibMath.floorDiv_l(new NtMaybe<>(), new NtMaybe<>()).has());
  }

  /** floorMod */
  @Test
  public void test_floorMod_l() {
    Assert.assertEquals(1L, LibMath.floorMod(1L, 2L));
    Assert.assertEquals(1L, (long) LibMath.floorMod_l(1L, new NtMaybe<>(2L)).get());
    Assert.assertEquals(1L, (long) LibMath.floorMod_l(new NtMaybe<>(1L), 2L).get());
    Assert.assertEquals(1L, (long) LibMath.floorMod_l(new NtMaybe<>(1L), new NtMaybe<>(2L)).get());
    Assert.assertFalse(LibMath.floorMod_l(new NtMaybe<>(1L), new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floorMod_l(new NtMaybe<>(), 2L).has());
    Assert.assertFalse(LibMath.floorMod_l(1L, new NtMaybe<>()).has());
    Assert.assertFalse(LibMath.floorMod_l(new NtMaybe<>(), new NtMaybe<>(2L)).has());
    Assert.assertFalse(LibMath.floorMod_l(new NtMaybe<>(), new NtMaybe<>()).has());
  }
}
