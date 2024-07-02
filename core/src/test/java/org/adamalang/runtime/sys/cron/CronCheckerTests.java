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
package org.adamalang.runtime.sys.cron;

import org.adamalang.runtime.natives.NtTime;
import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.reactives.RxTime;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CronCheckerTests {
  @Test
  public void daily_chicago_to_hawaii() {
    RxInt64 last = new RxInt64(null, 0L);
    long now = 1703013887615L;
    ZoneId sys = ZoneId.of("America/Chicago");
    ZoneId doc = ZoneId.of("US/Hawaii");
    { // catch up
      CronTask task = CronChecker.daily(last, now, 17, 30, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703129400000L, task.next);
      Assert.assertEquals("2023-12-20T17:30-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-20T21:30-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // prevent
      now += 60 * 1000;
      CronTask task = CronChecker.daily(last, now, 17, 30, sys, doc);
      Assert.assertFalse(task.fire);
      Assert.assertEquals(1703129400000L, task.next);
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // advance time
      now = 1703129400001L;
      CronTask task = CronChecker.daily(last, now, 17, 30, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703215800000L, task.next);
      Assert.assertEquals("2023-12-21T17:30-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-21T21:30-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703129400001L, (long) last.get());
    }
  }

  @Test
  public void daily_chicago_to_hawaii_rx() {
    RxInt64 last = new RxInt64(null, 0L);
    long now = 1703013887615L;
    ZoneId sys = ZoneId.of("America/Chicago");
    ZoneId doc = ZoneId.of("US/Hawaii");
    RxTime time = new RxTime(null, new NtTime(17, 30));
    { // catch up
      CronTask task = CronChecker.daily(last, now, time, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703129400000L, task.next);
      Assert.assertEquals("2023-12-20T17:30-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-20T21:30-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // prevent
      now += 60 * 1000;
      CronTask task = CronChecker.daily(last, now, time, sys, doc);
      Assert.assertFalse(task.fire);
      Assert.assertEquals(1703129400000L, task.next);
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // advance time
      now = 1703129400001L;
      CronTask task = CronChecker.daily(last, now, time, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703215800000L, task.next);
      Assert.assertEquals("2023-12-21T17:30-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-21T21:30-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703129400001L, (long) last.get());
    }
  }

  @Test
  public void hourly() {
    RxInt64 last = new RxInt64(null, 0L);
    long now = 1703013887615L;
    ZoneId sys = ZoneId.of("America/Chicago");
    ZoneId doc = ZoneId.of("US/Hawaii");
    { // catch up
      CronTask task = CronChecker.hourly(last, now, 17, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703017020000L, task.next);
      Assert.assertEquals("2023-12-19T10:17-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-19T14:17-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // prevent
      now += 45 * 60 * 1000;
      CronTask task = CronChecker.hourly(last, now, 17, sys, doc);
      Assert.assertFalse(task.fire);
      Assert.assertEquals(1703017020000L, task.next);
      Assert.assertEquals("2023-12-19T10:17-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-19T14:17-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // fire_again
      now += 45 * 60 * 1000;
      CronTask task = CronChecker.hourly(last, now, 17, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703020620000L, task.next);
      Assert.assertEquals("2023-12-19T11:17-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-19T15:17-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703019287615L, (long) last.get());
    }
  }

  @Test
  public void hourly_rx() {
    RxInt64 last = new RxInt64(null, 0L);
    RxInt32 min = new RxInt32(null, 17);
    long now = 1703013887615L;
    ZoneId sys = ZoneId.of("America/Chicago");
    ZoneId doc = ZoneId.of("US/Hawaii");
    { // catch up
      CronTask task = CronChecker.hourly(last, now, min, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703017020000L, task.next);
      Assert.assertEquals("2023-12-19T10:17-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-19T14:17-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // prevent
      now += 45 * 60 * 1000;
      CronTask task = CronChecker.hourly(last, now, min, sys, doc);
      Assert.assertFalse(task.fire);
      Assert.assertEquals(1703017020000L, task.next);
      Assert.assertEquals("2023-12-19T10:17-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-19T14:17-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // fire_again
      now += 45 * 60 * 1000;
      CronTask task = CronChecker.hourly(last, now, min, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703020620000L, task.next);
      Assert.assertEquals("2023-12-19T11:17-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-19T15:17-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703019287615L, (long) last.get());
    }
  }

  @Test
  public void monthly() {
    RxInt64 last = new RxInt64(null, 0L);
    long now = 1703013887615L;
    ZoneId sys = ZoneId.of("America/Chicago");
    ZoneId doc = ZoneId.of("US/Hawaii");
    { // catch up
      CronTask task = CronChecker.monthly(last, now, 5, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1704448800000L, task.next);
      Assert.assertEquals("2024-01-05T00:00-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2024-01-05T04:00-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // prevent
      now += 10 * 24 * 60 * 60 * 1000;
      CronTask task = CronChecker.monthly(last, now, 5, sys, doc);
      Assert.assertFalse(task.fire);
      Assert.assertEquals(1704448800000L, task.next);
      Assert.assertEquals("2024-01-05T00:00-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2024-01-05T04:00-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // fire_again
      now += 10 * 24 * 60 * 60 * 1000;
      CronTask task = CronChecker.monthly(last, now, 5, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1707127200000L, task.next);
      Assert.assertEquals("2024-02-05T00:00-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2024-02-05T04:00-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1704741887615L, (long) last.get());
    }
  }


  @Test
  public void monthly_rx() {
    RxInt64 last = new RxInt64(null, 0L);
    long now = 1703013887615L;
    ZoneId sys = ZoneId.of("America/Chicago");
    ZoneId doc = ZoneId.of("US/Hawaii");
    RxInt32 dom = new RxInt32(null, 5);
    { // catch up
      CronTask task = CronChecker.monthly(last, now, dom, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1704448800000L, task.next);
      Assert.assertEquals("2024-01-05T00:00-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2024-01-05T04:00-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // prevent
      now += 10 * 24 * 60 * 60 * 1000;
      CronTask task = CronChecker.monthly(last, now, dom, sys, doc);
      Assert.assertFalse(task.fire);
      Assert.assertEquals(1704448800000L, task.next);
      Assert.assertEquals("2024-01-05T00:00-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2024-01-05T04:00-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1703013887615L, (long) last.get());
    }
    { // fire_again
      now += 10 * 24 * 60 * 60 * 1000;
      CronTask task = CronChecker.monthly(last, now, dom, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1707127200000L, task.next);
      Assert.assertEquals("2024-02-05T00:00-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2024-02-05T04:00-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
      Assert.assertEquals(1704741887615L, (long) last.get());
    }
  }
}
