/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
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
  }
}
