/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.operations;

import org.junit.Assert;
import org.junit.Test;

public class CounterTests {
  @Test
  public void flow() {
    Counter c = new Counter();
    c.bump();
    c.bump();
    c.bump();
    Assert.assertEquals(3, c.getAndReset());
    Assert.assertEquals(0, c.getAndReset());
    c.bump();
    Assert.assertEquals(1, c.getAndReset());
  }
}
