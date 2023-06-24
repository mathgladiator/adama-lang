/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.RxParent;
import org.junit.Assert;

public class MockRxParent implements RxParent {
  public int dirtyCount;
  public boolean alive;

  public MockRxParent() {
    dirtyCount = 0;
    alive = true;
  }

  @Override
  public void __raiseDirty() {
    dirtyCount++;
  }

  public void assertDirtyCount(final int expected) {
    Assert.assertEquals(expected, dirtyCount);
  }

  @Override
  public boolean __isAlive() {
    return alive;
  }
}
