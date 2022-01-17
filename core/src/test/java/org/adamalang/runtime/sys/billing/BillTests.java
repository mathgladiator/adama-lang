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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

public class BillTests {
  @Test
  public void flow() {
    Bill bill =
        new Bill(42, 123, "space", "hash", new PredictiveInventory.Billing(100, 200, 42, 1000));
    Assert.assertEquals(42L, bill.time);
    Assert.assertEquals(123, bill.timeframe);
    Assert.assertEquals("space", bill.space);
    Assert.assertEquals("hash", bill.hash);
    Assert.assertEquals(100, bill.memory);
    Assert.assertEquals(200, bill.cpu);
    Assert.assertEquals(42, bill.count);
    Assert.assertEquals(1000, bill.messages);
  }

  @Test
  public void packings() {
    {
      Bill bill =
          new Bill(42, 123, "space", "hash", new PredictiveInventory.Billing(100, 200, 42, 1000));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\"]",
          bill.packup());
    }
    {
      Bill bill =
          new Bill(42, 123, "space", "hash", new PredictiveInventory.Billing(100, 0, 0, 0));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"0\",\"0\",\"0\"]",
          bill.packup());
    }
    {
      Bill bill =
          new Bill(42, 123, "space", "hash", new PredictiveInventory.Billing(0, 200, 0, 0));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"200\",\"0\",\"0\"]",
          bill.packup());
    }
    {
      Bill bill =
          new Bill(42, 123, "space", "hash", new PredictiveInventory.Billing(0, 0, 42, 0));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"42\",\"0\"]",
          bill.packup());
    }
    {
      Bill bill =
          new Bill(42, 123, "space", "hash", new PredictiveInventory.Billing(0, 0, 0, 1000));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"0\",\"1000\"]",
          bill.packup());
    }
    {
      Bill bill =
          new Bill(42, 123, "space", "hash", new PredictiveInventory.Billing(0, 0, 0, 0));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"0\",\"0\"]",
          bill.packup());
    }
  }

  @Test
  public void unpack() {
    Bill bill =
        new Bill(42, 123, "space", "hash", new PredictiveInventory.Billing(100, 200, 42, 1000));
    JsonStreamReader reader = new JsonStreamReader(bill.packup() + bill.packup() + bill.packup());
    Bill a = Bill.unpack(reader);
    Bill b = Bill.unpack(reader);
    Bill c = Bill.unpack(reader);
    Bill d = Bill.unpack(reader);
    Assert.assertEquals(42, b.time);
    Assert.assertEquals(42, c.time);
    Assert.assertEquals(42, a.time);
    Assert.assertEquals(123, a.timeframe);
    Assert.assertEquals("space", a.space);
    Assert.assertEquals("hash", a.hash);
    Assert.assertEquals(100, a.memory);
    Assert.assertEquals(200, a.cpu);
    Assert.assertEquals(42, a.count);
    Assert.assertEquals(1000, a.messages);
    Assert.assertNull(d);
  }

  @Test
  public void badversion() {
    JsonStreamReader reader = new JsonStreamReader("[\"v1\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\"]" + "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\"]" + "[\"v1\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\"]" + "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\"]");
    Bill bad1 = Bill.unpack(reader);
    Bill a = Bill.unpack(reader);
    Bill bad2 = Bill.unpack(reader);
    Bill b = Bill.unpack(reader);
    Assert.assertNull(bad1);
    Assert.assertNull(bad2);
    Assert.assertEquals(42, a.time);
    Assert.assertEquals(123, a.timeframe);
    Assert.assertEquals("space", a.space);
    Assert.assertEquals("hash", a.hash);
    Assert.assertEquals(100, a.memory);
    Assert.assertEquals(200, a.cpu);
    Assert.assertEquals(42, a.count);
    Assert.assertEquals(1000, a.messages);
    Assert.assertEquals(42, b.time);
    Assert.assertEquals(123, b.timeframe);
    Assert.assertEquals("space", b.space);
    Assert.assertEquals("hash", b.hash);
    Assert.assertEquals(100, b.memory);
    Assert.assertEquals(200, b.cpu);
    Assert.assertEquals(42, b.count);
    Assert.assertEquals(1000, b.messages);
  }
}
