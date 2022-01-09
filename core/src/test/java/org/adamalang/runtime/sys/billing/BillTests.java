/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.billing;

import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

public class BillTests {
  @Test
  public void flow() {
    Bill bill = new Bill("space", "hash", new PredictiveInventory.Billing(100, 200, 42, 1000));
    Assert.assertEquals("space", bill.space);
    Assert.assertEquals("hash", bill.hash);
    Assert.assertEquals(100, bill.memory);
    Assert.assertEquals(200, bill.cpu);
    Assert.assertEquals(42, bill.count);
    Assert.assertEquals(1000, bill.messages);
  }
}
