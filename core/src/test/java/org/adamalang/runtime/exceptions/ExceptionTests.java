/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.exceptions;

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
  }
}
