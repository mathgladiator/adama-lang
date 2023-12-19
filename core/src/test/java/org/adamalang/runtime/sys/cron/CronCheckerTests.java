/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

import org.adamalang.runtime.reactives.RxInt64;
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
    }
    { // prevent
      now += 60 * 1000;
      CronTask task = CronChecker.daily(last, now, 17, 30, sys, doc);
      Assert.assertFalse(task.fire);
      Assert.assertEquals(1703129400000L, task.next);
    }
    { // advance time
      now = 1703129400001L;
      CronTask task = CronChecker.daily(last, now, 17, 30, sys, doc);
      Assert.assertTrue(task.fire);
      Assert.assertEquals(1703215800000L, task.next);
      Assert.assertEquals("2023-12-21T17:30-10:00[US/Hawaii]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).withZoneSameInstant(doc).toString());
      Assert.assertEquals("2023-12-21T21:30-06:00[America/Chicago]", ZonedDateTime.ofInstant(Instant.ofEpochMilli(task.next), sys).toString());
    }
  }
}
