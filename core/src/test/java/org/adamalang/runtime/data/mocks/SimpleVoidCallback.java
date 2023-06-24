/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SimpleVoidCallback implements Callback<Void> {
  private boolean success;
  private int count;
  private int reason;
  private CountDownLatch latch;

  public SimpleVoidCallback() {
    this.latch = new CountDownLatch(1);
    this.success = false;
    this.count = 0;
    this.reason = 0;
  }

  @Override
  public synchronized void success(Void value) {
    count++;
    success = true;
    latch.countDown();
  }

  @Override
  public synchronized void failure(ErrorCodeException ex) {
    System.err.println("Failure:" + ex.code);
    count++;
    success = false;
    reason = ex.code;
    latch.countDown();
  }

  public void assertSuccess() throws Exception {
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    synchronized (this) {
      Assert.assertEquals(1, count);
      Assert.assertTrue(success);
    }
  }

  public void assertFailure(int code) throws Exception {
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    synchronized (this) {
      Assert.assertEquals(1, count);
      Assert.assertFalse(success);
      Assert.assertEquals(code, this.reason);
    }
  }
}
