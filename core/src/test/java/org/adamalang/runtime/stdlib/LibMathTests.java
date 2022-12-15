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

import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class LibMathTests {

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
}
