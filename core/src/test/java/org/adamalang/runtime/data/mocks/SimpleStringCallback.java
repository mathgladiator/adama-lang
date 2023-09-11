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

public class SimpleStringCallback implements Callback<String> {
  public String value;
  private boolean success;
  private int count;
  private int reason;
  private CountDownLatch latch;

  public SimpleStringCallback() {
    this.value = null;
    this.success = false;
    this.count = 0;
    this.reason = -1;
    this.latch = new CountDownLatch(1);
  }

  @Override
  public synchronized void success(String value) {
    this.value = value;
    this.success = true;
    this.count++;
    latch.countDown();
  }

  @Override
  public synchronized void failure(ErrorCodeException ex) {
    this.reason = ex.code;
    this.success = false;
    this.count++;
    latch.countDown();
  }

  public void assertSuccess(String expected) throws Exception {
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    synchronized (this) {
      Assert.assertEquals(1, count);
      Assert.assertTrue(success);
      Assert.assertEquals(expected, this.value);
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
