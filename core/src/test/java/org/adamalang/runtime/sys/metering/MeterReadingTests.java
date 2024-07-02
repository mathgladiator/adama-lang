/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
        new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(100, 200, 42, 1000, 13, 123, 456, 789, 13, 420));
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
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(100, 200, 42, 1000, 13, 123, 456, 789, 13, 420));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"13\",\"123\",\"456\",\"789\",\"13\",\"420\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(100, 0, 0, 0, 0, 123, 456, 789, 13, 420));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"0\",\"0\",\"0\",\"0\",\"123\",\"456\",\"789\",\"13\",\"420\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(0, 200, 0, 0, 0, 123, 456, 789, 13, 420));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"200\",\"0\",\"0\",\"0\",\"123\",\"456\",\"789\",\"13\",\"420\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(0, 0, 42, 0, 0, 123, 456, 789, 13, 420));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"42\",\"0\",\"0\",\"123\",\"456\",\"789\",\"13\",\"420\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(0, 0, 0, 1000, 0, 123, 456, 789, 13, 420));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"0\",\"1000\",\"0\",\"123\",\"456\",\"789\",\"13\",\"420\"]",
          meterReading.packup());
    }
    {
      MeterReading meterReading =
          new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(0, 0, 0, 0, 13, 123, 456, 789, 13, 420));
      Assert.assertEquals(
          "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"0\",\"0\",\"0\",\"0\",\"13\",\"123\",\"456\",\"789\",\"13\",\"420\"]",
          meterReading.packup());
    }
  }

  @Test
  public void unpack() {
    MeterReading meterReading =
        new MeterReading(42, 123, "space", "hash", new PredictiveInventory.MeteringSample(100, 200, 42, 1000, 13, 123, 456, 789, 13, 420));
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
            "[\"v1\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"17\",\"13\",\"420\"]"
                + "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"17\", \"123\", \"456\", \"1313\",\"13\",\"420\"]"
                + "[\"v1\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"17\",\"13\",\"420\"]"
                + "[\"v0\",\"42\",\"123\",\"space\",\"hash\",\"100\",\"200\",\"42\",\"1000\",\"17\", \"123\", \"456\", \"1313\",\"13\",\"420\"]");
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
    Assert.assertEquals(13, a.cpu_ms);
    Assert.assertEquals(420, a.backup_bytes_hours);
    Assert.assertEquals(42, b.time);
    Assert.assertEquals(123, b.timeframe);
    Assert.assertEquals("space", b.space);
    Assert.assertEquals("hash", b.hash);
    Assert.assertEquals(100, b.memory);
    Assert.assertEquals(200, b.cpu);
    Assert.assertEquals(42, b.count);
    Assert.assertEquals(1000, b.messages);
    Assert.assertEquals(17, b.connections);
    Assert.assertEquals(13, a.cpu_ms);
    Assert.assertEquals(420, a.backup_bytes_hours);
  }
}
