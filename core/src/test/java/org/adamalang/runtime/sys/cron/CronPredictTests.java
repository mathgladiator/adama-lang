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

import org.adamalang.runtime.natives.NtTime;
import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.reactives.RxTime;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;

public class CronPredictTests {
  @Test
  public void merge_sanity() {
    Assert.assertEquals(4L, (long) CronPredict.merge(4L, 5L));
    Assert.assertEquals(4L, (long) CronPredict.merge(4L, null));
    Assert.assertEquals(4L, (long) CronPredict.merge(null, 4L));
  }
  @Test
  public void predict_hourly() {
    Assert.assertEquals(3012385L, (long) CronPredict.hourly(null, 1703013887615L, 15, ZoneId.of("America/Chicago"), ZoneId.of("US/Hawaii")));
  }
  @Test
  public void predict_hourly_rx() {
    Assert.assertEquals(3012385L, (long) CronPredict.hourly(null, 1703013887615L, new RxInt32(null, 15), ZoneId.of("America/Chicago"), ZoneId.of("US/Hawaii")));
  }
  @Test
  public void predict_daily() {
    Assert.assertEquals(115512385L, (long) CronPredict.daily(null, 1703013887615L, 17, 30, ZoneId.of("America/Chicago"), ZoneId.of("US/Hawaii")));
  }
  @Test
  public void predict_daily_rx() {
    Assert.assertEquals(115512385L, (long) CronPredict.daily(null, 1703013887615L, new RxTime(null, new NtTime(17, 30)), ZoneId.of("America/Chicago"), ZoneId.of("US/Hawaii")));
  }
  @Test
  public void predict_monthly() {
    Assert.assertEquals(2298912385L, (long) CronPredict.monthly(null, 1703013887615L, 15, ZoneId.of("America/Chicago"), ZoneId.of("US/Hawaii")));
  }
  @Test
  public void predict_monthly_rx() {
    Assert.assertEquals(2298912385L, (long) CronPredict.monthly(null, 1703013887615L, new RxInt32(null, 15), ZoneId.of("America/Chicago"), ZoneId.of("US/Hawaii")));
  }
}
