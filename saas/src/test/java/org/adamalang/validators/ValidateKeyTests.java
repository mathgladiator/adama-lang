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

public class ValidateKeyTests {
  @Test
  public void tooLong() throws Exception {
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < 511; k++) {
      sb.append("a");
      ValidateKey.validate(sb.toString());
    }
    try {
      sb.append("a");
      ValidateKey.validate(sb.toString());
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(946192, ece.code);
    }
  }

  @Test
  public void tooShort() {
    try {
      ValidateKey.validate("");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(919676, ece.code);
    }
  }

  @Test
  public void tooComplex() {
    try {
      ValidateKey.validate("#&");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(946192, ece.code);
    }
  }

  @Test
  public void good() throws Exception {
    ValidateKey.validate("simple");
  }
}
