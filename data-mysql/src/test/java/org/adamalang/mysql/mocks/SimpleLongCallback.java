/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

public class SimpleLongCallback implements Callback<Long> {
  public Long value;
  private boolean success;
  private int count;
  private int reason;

  public SimpleLongCallback() {
    this.value = null;
    this.success = false;
    this.count = 0;
    this.reason = -1;
  }

  @Override
  public void success(Long value) {
    this.value = value;
    this.success = true;
    this.count++;
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.reason = ex.code;
    this.success = false;
    this.count++;
  }

  public void assertSuccess(long value) {
    Assert.assertEquals(1, count);
    Assert.assertTrue(success);
    Assert.assertEquals(value, (long) this.value);
  }

  public void assertFailure(int code) {
    Assert.assertEquals(1, count);
    Assert.assertFalse(success);
    Assert.assertEquals(code, this.reason);
  }
}
