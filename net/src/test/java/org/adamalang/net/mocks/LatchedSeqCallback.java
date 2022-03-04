/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.mocks;

import org.adamalang.net.client.contracts.SeqCallback;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LatchedSeqCallback implements SeqCallback {
  private final CountDownLatch latch;
  private boolean error;
  private int value;

  public LatchedSeqCallback() {
    latch = new CountDownLatch(1);
    this.error = false;
    this.value = 0;
  }

  @Override
  public void success(int seq) {
    this.error = false;
    this.value = seq;
    latch.countDown();
  }

  @Override
  public void error(int code) {
    this.error = true;
    this.value = code;
    latch.countDown();
  }

  public void assertSuccess(int v) {
    try {
      Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertFalse(error);
      Assert.assertEquals(v, this.value);
    } catch (Exception ex) {
      Assert.fail();
    }
  }

  public void assertFail(int c) {
    try {
      Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(error);
      Assert.assertEquals(c, this.value);
    } catch (Exception ex) {
      Assert.fail();
    }
  }
}
