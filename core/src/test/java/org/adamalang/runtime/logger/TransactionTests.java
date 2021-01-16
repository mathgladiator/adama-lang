/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.junit.Test;

public class TransactionTests {
  @Test
  public void flow() {
    new Transaction(-1, "{}", "{\"x\":1}", "{\"x\":0}", new TransactionResult(true, 0, 0)).toString();
    new Transaction(-1, "{}", "{\"x\":1}", "{\"x\":0}", new TransactionResult(false, 0, 0)).toString();
  }
}
