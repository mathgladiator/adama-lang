package org.adamalang.net.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LatchedVoidCallback implements Callback<Void> {
  private final CountDownLatch latch;
  private boolean error;
  private int value;

  public LatchedVoidCallback() {
    latch = new CountDownLatch(1);
    this.error = false;
    this.value = 0;
  }

  @Override
  public void success(Void seq) {
    this.error = false;
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.error = true;
    this.value = ex.code;
    latch.countDown();
  }

  public void assertSuccess() {
    try {
      Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertFalse(error);
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
