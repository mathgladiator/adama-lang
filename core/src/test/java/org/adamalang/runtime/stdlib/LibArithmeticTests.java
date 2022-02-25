/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class LibArithmeticTests {
  @Test
  public void doubleDivision() {
    Assert.assertEquals(0.5, LibArithmetic.Divide.DD(1, 2.0).get(), 0.01);
    Assert.assertEquals(0.5, LibArithmetic.Divide.mDD(new NtMaybe<>(1.0), 2).get(), 0.01);
    Assert.assertEquals(0.5, LibArithmetic.Divide.DmD(1.0, new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertEquals(
        0.5, LibArithmetic.Divide.mDmD(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
    Assert.assertFalse(LibArithmetic.Divide.mDD(new NtMaybe<>(), 2).has());
    Assert.assertFalse(LibArithmetic.Divide.DmD(1.0, new NtMaybe<>()).has());
    Assert.assertFalse(LibArithmetic.Divide.mDmD(new NtMaybe<>(), new NtMaybe<>()).has());
    Assert.assertFalse(LibArithmetic.Divide.mDmD(new NtMaybe<>(1.0), new NtMaybe<>()).has());
    Assert.assertFalse(LibArithmetic.Divide.mDmD(new NtMaybe<>(), new NtMaybe<>(1.0)).has());
    Assert.assertFalse(LibArithmetic.Divide.DD(1, 0.0).has());
  }

  @Test
  public void intDivision() {
    Assert.assertEquals(0.5, LibArithmetic.Divide.II(1, 2).get(), 0.01);
    Assert.assertFalse(LibArithmetic.Divide.II(1, 0).has());
  }

  @Test
  public void generateTestCase() {
    String[] x = new String[] { "1", "1L", "0.5", "(1 / 2)", "(1 / 0)", "@i", "(1 / @i)", "(@i / 0)"};
    int k = 0;
    for (String a : x) {
      for (String b : x) {
        System.out.println("  public formula f" + k + " = " + a + " / " + b + ";");
        k++;
      }
    }
  }
}
