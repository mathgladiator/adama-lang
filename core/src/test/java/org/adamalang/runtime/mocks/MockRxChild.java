/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
