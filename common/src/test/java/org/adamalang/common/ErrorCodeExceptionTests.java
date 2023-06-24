/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class ErrorCodeExceptionTests {
  @Test
  public void coverage() {
    ExceptionLogger logger = (t, errorCode) -> {};
    ErrorCodeException ex1 = new ErrorCodeException(42);
    ErrorCodeException ex2 = new ErrorCodeException(500, new NullPointerException());
    ErrorCodeException ex3 = new ErrorCodeException(4242, "nope");
    Assert.assertEquals(42, ErrorCodeException.detectOrWrap(100, ex1, logger).code);
    Assert.assertEquals(
        500, ErrorCodeException.detectOrWrap(100, new RuntimeException(ex2), logger).code);
    Assert.assertEquals(
        100, ErrorCodeException.detectOrWrap(100, new NullPointerException(), logger).code);
    Assert.assertEquals(42, ex1.code);
    Assert.assertEquals(500, ex2.code);
    Assert.assertEquals(4242, ex3.code);
  }
}
