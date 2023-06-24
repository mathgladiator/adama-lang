/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class SampleServiceTests {
  @Test
  public void coverage() {
    SampleService ss = new SampleService();
    AtomicBoolean called = new AtomicBoolean(false);
    ss.request("method", "{}", new Callback<String>() {
      @Override
      public void success(String value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        called.set(true);
        Assert.assertEquals(888888, ex.code);
      }
    });
    Assert.assertTrue(called.get());
  }
}
