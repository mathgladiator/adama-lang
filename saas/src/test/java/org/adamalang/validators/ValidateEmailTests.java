/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.validators;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class ValidateEmailTests {
  @Test
  public void coverage() throws Exception {
    ValidateEmail.validate("x@x.com");
    try {
      ValidateEmail.validate("x@x.com.");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(905293, ece.code);
    }
  }
}
