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

import org.junit.Assert;
import org.junit.Test;

public class LibMathTests {
  @Test
  public void basics() {
    Assert.assertEquals(1, LibMath.floor(1.6));
    Assert.assertEquals(2, LibMath.ceil(1.6));
    Assert.assertEquals(2, LibMath.round(1.6));
    Assert.assertEquals(1, LibMath.round(1.4));
    Assert.assertTrue(LibMath.near(0.3, 0.2 + 0.1));
    Assert.assertTrue(LibMath.near(1.4142135623730950488016887242097, LibMath.SQRT2));
    Assert.assertTrue(LibMath.near(0.3382, LibMath.roundTo(0.338198742, 4)));
    Assert.assertTrue(LibMath.near(0.338, LibMath.roundTo(0.338198742, 3)));
    Assert.assertTrue(LibMath.near(0.34, LibMath.roundTo(0.338198742, 2)));
    Assert.assertTrue(LibMath.near(0.3, LibMath.roundTo(0.338198742, 1)));
    Assert.assertTrue(LibMath.near(0.0, LibMath.roundTo(0.338198742, 0)));
  }
}
