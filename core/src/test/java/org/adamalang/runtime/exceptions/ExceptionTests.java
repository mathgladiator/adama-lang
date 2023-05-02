/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.exceptions;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class ExceptionTests {
  @Test
  public void detectOrWrap() {
    final var gwee = new GoodwillExhaustedException(0, 1, 2, 3);
    Assert.assertEquals("Good will exhausted:0,1 --> 2,3", gwee.getMessage());
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

  @Test
  public void cons() {
    new AbortMessageException();
    new RetryProgressException(null);
    new ComputeBlockedException(NtPrincipal.NO_ONE, "foo");
    new ComputeBlockedException();
    new ErrorCodeException(14, "Nope");
    new PerformDocumentRewindException(100);
    new PerformDocumentDeleteException();
  }
}
