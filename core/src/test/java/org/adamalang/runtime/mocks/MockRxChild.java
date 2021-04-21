/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.RxChild;
import org.junit.Assert;

public class MockRxChild implements RxChild {
  public boolean alive;
  public int invalidCount;

  public MockRxChild() {
    invalidCount = 0;
    alive = true;
  }

  @Override
  public boolean __raiseInvalid() {
    invalidCount++;
    return alive;
  }

  public void assertInvalidateCount(final int expected) {
    Assert.assertEquals(expected, invalidCount);
  }
}
