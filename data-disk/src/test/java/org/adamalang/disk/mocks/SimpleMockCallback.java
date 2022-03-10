/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SimpleMockCallback implements Callback<Void> {
  private boolean success;
  private int count;
  private int reason;
  private CountDownLatch latch;

  public SimpleMockCallback() {
    this.latch = new CountDownLatch(1);
    this.success = false;
    this.count = 0;
    this.reason = 0;
  }

  @Override
  public synchronized void success(Void value) {
    latch.countDown();
    count++;
    success = true;
  }

  @Override
  public synchronized void failure(ErrorCodeException ex) {
    latch.countDown();
    ex.printStackTrace();
    count++;
    success = false;
    reason = ex.code;
  }

  public void await() throws Exception {
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
  }

  public synchronized void assertSuccess() {
    Assert.assertEquals(1, count);
    Assert.assertTrue(success);
  }

  public synchronized void assertFailure(int code) {
    Assert.assertEquals(1, count);
    Assert.assertFalse(success);
    Assert.assertEquals(code, this.reason);
  }
}
