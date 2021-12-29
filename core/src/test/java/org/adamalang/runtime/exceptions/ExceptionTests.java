/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.exceptions;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class ExceptionTests {
  @Test
  public void coverage() {
    final var gwee = new GoodwillExhaustedException(0, 1, 2, 3);
    Assert.assertEquals("Good will exhausted:0,1 --> 2,3", gwee.getMessage());
    new AbortMessageException();
    new RetryProgressException(null);
    new ComputeBlockedException(NtClient.NO_ONE, "foo");
    new ErrorCodeException(14, "Nope");

    ErrorCodeException eee = new ErrorCodeException(14, "Nope");
    Assert.assertTrue(
        eee
            == ErrorCodeException.detectOrWrap(
                5400,
                eee,
                new ExceptionLogger() {
                  @Override
                  public void convertedToErrorCode(Throwable t, int errorCode) {
                    t.printStackTrace();
                  }
                }));
  }
}
