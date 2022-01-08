/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.mocks;

import org.adamalang.grpc.client.contracts.CreateCallback;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AssertCreateFailure implements CreateCallback {

  private final CountDownLatch latch;
  public int code;

  public AssertCreateFailure() {
    this.latch = new CountDownLatch(1);
  }

  @Override
  public void created() {
    latch.countDown();
    Assert.fail();
  }

  @Override
  public void error(int code) {
    this.code = code;
    latch.countDown();
  }

  public void await(int expectedCode) {
    try {
      Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(expectedCode, code);
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }
}
