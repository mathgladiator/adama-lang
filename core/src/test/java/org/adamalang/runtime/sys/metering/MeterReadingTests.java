/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.metering;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

public class MeterReadingTests {
  @Test
  public void flow() {
    MeterReading meterReading =
        new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(100, 200, 42, 1000, 13, 123, 456, 789));
    Assert.assertEquals(42L, meterReading.time);
    Assert.assertEquals(123, meterReading.timeframe);
    Assert.assertEquals("space", meterReading.space);
    Assert.assertEquals("hash", meterReading.hash);
    Assert.assertEquals(100, meterReading.memory);
    Assert.assertEquals(200, meterReading.cpu);
    Assert.assertEquals(42, meterReading.count);
    Assert.assertEquals(1000, meterReading.messages);
  }

  @Test
  public void packings() {
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(100, 200, 42, 1000, 13, 123, 456, 789));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"13\",\"123\",\"456\",\"789\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(100, 0, 0, 0, 0, 123, 456, 789));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"0\",\"0\",\"0\",\"0\",\"123\",\"456\",\"789\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(0, 200, 0, 0, 0, 123, 456, 789));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"200\",\"0\",\"0\",\"0\",\"123\",\"456\",\"789\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(0, 0, 42, 0, 0, 123, 456, 789));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"42\",\"0\",\"0\",\"123\",\"456\",\"789\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(0, 0, 0, 1000, 0, 123, 456, 789));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"0\",\"1000\",\"0\",\"123\",\"456\",\"789\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(0, 0, 0, 0, 13, 123, 456, 789));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"0\",\"0\",\"13\",\"123\",\"456\",\"789\"]",
          meterReading.packup());
    }
  }

  @Test
  public void unpack() {
    MeterReading meterReading =
        new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(100, 200, 42, 1000, 13, 123, 456, 789));
    JsonStreamReader reader = new JsonStreamReader(meterReading.packup() + meterReading.packup() + meterReading.packup());
    MeterReading a = MeterReading.unpack(reader);
    MeterReading b = MeterReading.unpack(reader);
    MeterReading c = MeterReading.unpack(reader);
    MeterReading d = MeterReading.unpack(reader);
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
    Assert.assertEquals(13, a.connections);
    Assert.assertNull(d);
  }

  @Test
  public void badversion() {
    JsonStreamReader reader =
        new JsonStreamReader(
            "[\"v1\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"17\"]"
                + "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"17\", \"123\", \"456\", \"1313\"]"
                + "[\"v1\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"17\"]"
                + "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"17\", \"123\", \"456\", \"1313\"]");
    MeterReading bad1 = MeterReading.unpack(reader);
    MeterReading a = MeterReading.unpack(reader);
    MeterReading bad2 = MeterReading.unpack(reader);
    MeterReading b = MeterReading.unpack(reader);
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
    Assert.assertEquals(17, a.connections);
    Assert.assertEquals(42, b.time);
    Assert.assertEquals(123, b.timeframe);
    Assert.assertEquals("space", b.space);
    Assert.assertEquals("hash", b.hash);
    Assert.assertEquals(100, b.memory);
    Assert.assertEquals(200, b.cpu);
    Assert.assertEquals(42, b.count);
    Assert.assertEquals(1000, b.messages);
    Assert.assertEquals(17, b.connections);
  }
}
