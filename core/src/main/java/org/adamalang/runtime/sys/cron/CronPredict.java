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

import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.reactives.RxTime;

import java.time.ZoneId;

public class CronPredict {
  public static Long merge(Long a, Long b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    return Math.min(a, b);
  }

  public static Long predictWhen(long now, CronTask task) {
    if (!task.fire) {
      long delta = task.next - now;
      if (delta > 0) {
        return delta;
      }
    }
    return 0L;
  }

  public static Long hourly(Long prior, long currentTime, int minutes, ZoneId sysTimeZone, ZoneId docTimeZone) {
    return merge(prior, predictWhen(currentTime, CronChecker.hourly(new RxInt64(null, currentTime), currentTime, minutes, sysTimeZone, docTimeZone)));
  }

  public static Long hourly(Long prior, long currentTime, RxInt32 minutes, ZoneId sysTimeZone, ZoneId docTimeZone) {
    return merge(prior, predictWhen(currentTime, CronChecker.hourly(new RxInt64(null, currentTime), currentTime, minutes, sysTimeZone, docTimeZone)));
  }

  public static Long daily(Long prior, long currentTime, int hour, int minute, ZoneId sysTimeZone, ZoneId docTimeZone) {
    return merge(prior, predictWhen(currentTime, CronChecker.daily(new RxInt64(null, currentTime), currentTime, hour, minute, sysTimeZone, docTimeZone)));
  }

  public static Long daily(Long prior, long currentTime, RxTime time, ZoneId sysTimeZone, ZoneId docTimeZone) {
    return merge(prior, predictWhen(currentTime, CronChecker.daily(new RxInt64(null, currentTime), currentTime, time, sysTimeZone, docTimeZone)));
  }

  public static Long monthly(Long prior, long currentTime, int dayOfMonthGiven, ZoneId sysTimeZone, ZoneId docTimeZone) {
    return merge(prior, predictWhen(currentTime, CronChecker.monthly(new RxInt64(null, currentTime), currentTime, dayOfMonthGiven, sysTimeZone, docTimeZone)));
  }

  public static Long monthly(Long prior, long currentTime, RxInt32 dayOfMonth, ZoneId sysTimeZone, ZoneId docTimeZone) {
    return merge(prior, predictWhen(currentTime, CronChecker.monthly(new RxInt64(null, currentTime), currentTime, dayOfMonth, sysTimeZone, docTimeZone)));
  }
}
