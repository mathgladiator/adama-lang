/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
