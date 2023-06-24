/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class DelayParentTests {
  @Test
  public void flow_a() {
    AtomicBoolean success = new AtomicBoolean(false);
    DelayParent dp = new DelayParent();
    dp.__raiseDirty();
    dp.bind(() -> success.set(true));
    Assert.assertTrue(success.get());
    Assert.assertTrue(dp.__isAlive());
  }

  @Test
  public void flow_b() {
    AtomicBoolean success = new AtomicBoolean(false);
    DelayParent dp = new DelayParent();
    dp.bind(() -> success.set(true));
    dp.__raiseDirty();
    Assert.assertTrue(success.get());
    Assert.assertTrue(dp.__isAlive());
  }
}
