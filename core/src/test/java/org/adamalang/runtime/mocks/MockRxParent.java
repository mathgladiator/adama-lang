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

import org.adamalang.runtime.contracts.RxParent;
import org.junit.Assert;

public class MockRxParent implements RxParent {
  public int dirtyCount;

  public MockRxParent() {
    dirtyCount = 0;
  }

  @Override
  public void __raiseDirty() {
    dirtyCount++;
  }

  public void assertDirtyCount(final int expected) {
    Assert.assertEquals(expected, dirtyCount);
  }
}
