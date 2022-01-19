/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.validators;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class ValidateChannelTests {
  @Test
  public void empty() {
    try {
      ValidateChannel.validate("");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(950399, ece.code);
    }
  }

  @Test
  public void badstart() {
    try {
      ValidateChannel.validate("-");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(908415, ece.code);
    }
  }

  @Test
  public void badmiddle() {
    try {
      ValidateChannel.validate("a-");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(967804, ece.code);
    }
  }

  @Test
  public void good() throws Exception{
    ValidateChannel.validate("fooninja");
  }
}
