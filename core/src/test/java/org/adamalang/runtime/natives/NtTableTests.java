/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.mocks.MockMessage;
import org.junit.Assert;
import org.junit.Test;

public class NtTableTests {
  @Test
  public void flow() {
    final var table = new NtTable<>(MockMessage::new);
    new NtTable<>(table);
    table.make();
    Assert.assertEquals(1, table.size());
    table.make();
    Assert.assertEquals(2, table.size());
    table.delete();
    Assert.assertEquals(0, table.size());
    Assert.assertEquals(0, table.iterate(false).size());
    table.make();
    table.make();
    table.make();
    Assert.assertEquals(3, table.iterate(false).size());
    table.__raiseInvalid();
  }
}
