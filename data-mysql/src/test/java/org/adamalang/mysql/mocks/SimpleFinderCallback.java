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
import org.adamalang.runtime.data.FinderService;
import org.junit.Assert;

public class SimpleFinderCallback implements Callback<FinderService.Result> {
  public FinderService.Result value;
  public int reads;
  private boolean success;
  private int count;
  private int reason;

  public SimpleFinderCallback() {
    this.success = false;
    this.count = 0;
    this.reason = 0;
    this.reads = 0;
  }

  @Override
  public void success(FinderService.Result value) {
    this.value = value;
    count++;
    success = true;
  }

  @Override
  public void failure(ErrorCodeException ex) {
    count++;
    success = false;
    reason = ex.code;
  }

  public void assertSuccess(FinderService.Location location, String machine, String archiveKey) {
    Assert.assertEquals(1, count);
    Assert.assertTrue(success);
    Assert.assertEquals(location, value.location);
    Assert.assertEquals(machine, value.machine);
    Assert.assertEquals(archiveKey, value.archiveKey);
  }

  public void assertFailure(int code) {
    Assert.assertEquals(1, count);
    Assert.assertFalse(success);
    Assert.assertEquals(code, this.reason);
  }
}
