/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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
