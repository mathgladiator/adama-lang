/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class TransactionResultTests {
  @Test
  public void coverage() {
    new TransactionResult(true, 1, 0).dumpInto(Utility.createObjectNode());
  }

  @Test
  public void from() {
    final var x = TransactionResult.from(Utility.parseJsonObject("{\"needsInvalidation\":true,\"whenToInvalidMilliseconds\":42}"));
    Assert.assertTrue(x.needsInvalidation);
    Assert.assertEquals(42, x.whenToInvalidMilliseconds);
  }
}
