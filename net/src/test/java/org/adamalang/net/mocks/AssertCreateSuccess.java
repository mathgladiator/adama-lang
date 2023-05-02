/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AssertCreateSuccess implements Callback<Void> {
  private final CountDownLatch latch;
  private boolean success;

  public AssertCreateSuccess() {
    this.latch = new CountDownLatch(1);
    this.success = false;
  }

  @Override
  public void success(Void value) {
    success = true;
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    latch.countDown();
  }

  public void await() {
    try {
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(success);
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }
}
