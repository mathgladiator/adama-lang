/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtComplexTests {
  @Test
  public void equals() {
    NtComplex a = new NtComplex(1.2, 3.4);
    NtComplex b = new NtComplex(1.2, 3.4);
    NtComplex c = new NtComplex(3.4, -1.2);
    Assert.assertEquals(a, a);
    Assert.assertEquals(a, b);
    Assert.assertNotEquals(a, c);
    Assert.assertNotEquals(a, "z");
    Assert.assertNotEquals("z", a);
  }

  @Test
  public void str() {
    NtComplex a = new NtComplex(1.2, 3.4);
    Assert.assertEquals("1.2 3.4i", a.toString());
    Assert.assertEquals(16, a.memory());
    Assert.assertEquals("0.09230769230769231 -0.26153846153846155i", a.recip().toString());
  }

  @Test
  public void zero() {
    Assert.assertTrue(new NtComplex(0.0, 0.0).zero());
  }
}
