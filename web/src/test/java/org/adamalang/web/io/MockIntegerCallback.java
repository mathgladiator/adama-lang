/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.io;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockIntegerCallback implements Callback<Integer> {
  public Integer response;
  public ErrorCodeException ex;
  private CountDownLatch done;
  public MockIntegerCallback() {
    this.done = new CountDownLatch(1);
  }

  public void awaitDone() throws Exception {
    Assert.assertTrue(done.await(1000, TimeUnit.MILLISECONDS));
  }

  public void assertErrorCode(int code) {
    Assert.assertNull(response);
    Assert.assertNotNull(ex);
    Assert.assertEquals(code, ex.code);
  }

  public void assertValue(int code) {
    Assert.assertNull(ex);
    Assert.assertNotNull(response);
    Assert.assertEquals(code, (int) response);
  }

  @Override
  public void success(Integer value) {
    this.response = value;
    done.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.ex = ex;
    done.countDown();
  }
}
