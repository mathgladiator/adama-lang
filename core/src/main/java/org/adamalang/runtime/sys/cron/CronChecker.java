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
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.reactives.RxTime;

import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/** the various rules for cron jobs */
public class CronChecker {

  public static CronTask hourly(RxInt64 lastFired, long currentTime, int minutes, ZoneId sysTimeZone, ZoneId docTimeZone) {
    // get the time we last fired within the document's time zone
    ZonedDateTime lastFiredDocTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastFired.get().longValue()), sysTimeZone).withZoneSameInstant(docTimeZone);
    ZonedDateTime currentDocTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTime), sysTimeZone).withZoneSameInstant(docTimeZone);
    ZonedDateTime futureFire = lastFiredDocTime.truncatedTo(ChronoUnit.HOURS).plusHours(1).plusMinutes(minutes);
    if (futureFire.isBefore(currentDocTime)) {
      ZonedDateTime nextFire = currentDocTime.truncatedTo(ChronoUnit.HOURS).plusHours(1).plusMinutes(minutes);
      lastFired.set(currentTime);
      return new CronTask(true, nextFire.withZoneSameInstant(sysTimeZone).toInstant().toEpochMilli());
    } else {
      return new CronTask(false, futureFire.withZoneSameInstant(sysTimeZone).toInstant().toEpochMilli());
    }
  }

  public static CronTask hourly(RxInt64 lastFired, long currentTime, RxInt32 minutes, ZoneId sysTimeZone, ZoneId docTimeZone) {
    return hourly(lastFired, currentTime, minutes.get(), sysTimeZone, docTimeZone);
  }


  public static CronTask daily(RxInt64 lastFired, long currentTime, int hour, int minute, ZoneId sysTimeZone, ZoneId docTimeZone) {
    // get the time we last fired within the document's time zone
    ZonedDateTime lastFiredDocTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastFired.get().longValue()), sysTimeZone).withZoneSameInstant(docTimeZone);
    ZonedDateTime currentDocTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTime), sysTimeZone).withZoneSameInstant(docTimeZone);
    ZonedDateTime futureFire = lastFiredDocTime.plusDays(1).truncatedTo(ChronoUnit.DAYS).plusHours(hour).plusMinutes(minute);
    if (futureFire.isBefore(currentDocTime)) {
      ZonedDateTime nextFire = currentDocTime.truncatedTo(ChronoUnit.DAYS).plusDays(1).plusHours(hour).plusMinutes(minute);
      lastFired.set(currentTime);
      return new CronTask(true, nextFire.withZoneSameInstant(sysTimeZone).toInstant().toEpochMilli());
    } else {
      return new CronTask(false, futureFire.withZoneSameInstant(sysTimeZone).toInstant().toEpochMilli());
    }
  }

  public static CronTask daily(RxInt64 lastFired, long currentTime, RxTime time, ZoneId sysTimeZone, ZoneId docTimeZone) {
    NtTime t = time.get();
    return daily(lastFired, currentTime, t.hour, t.minute, sysTimeZone, docTimeZone);
  }

  public static CronTask monthly(RxInt64 lastFired, long currentTime, int dayOfMonthGiven, ZoneId sysTimeZone, ZoneId docTimeZone) {
    // get the time we last fired within the document's time zone
    ZonedDateTime lastFiredDocTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastFired.get().longValue()), sysTimeZone).withZoneSameInstant(docTimeZone);
    ZonedDateTime currentDocTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTime), sysTimeZone).withZoneSameInstant(docTimeZone);
    ZonedDateTime futureFireBase = lastFiredDocTime.plusMonths(1).truncatedTo(ChronoUnit.DAYS);
    int max = futureFireBase.getMonth().maxLength();
    if (futureFireBase.getMonth() == Month.FEBRUARY) {
      max = 28; // we don't want to deal with the 29th
    }
    int dayOfMonth = Math.min(Math.max(dayOfMonthGiven, 1), max);
    ZonedDateTime futureFire = futureFireBase.withDayOfMonth(dayOfMonth);
    if (futureFire.isBefore(currentDocTime)) {
      ZonedDateTime nextFire = currentDocTime.plusMonths(1).truncatedTo(ChronoUnit.DAYS).withDayOfMonth(dayOfMonthGiven);
      lastFired.set(currentTime);
      return new CronTask(true, nextFire.withZoneSameInstant(sysTimeZone).toInstant().toEpochMilli());
    } else {
      return new CronTask(false, futureFire.withZoneSameInstant(sysTimeZone).toInstant().toEpochMilli());
    }
  }

  public static CronTask monthly(RxInt64 lastFired, long currentTime, RxInt32 dayOfMonth, ZoneId sysTimeZone, ZoneId docTimeZone) {
    return monthly(lastFired, currentTime, dayOfMonth.get(), sysTimeZone, docTimeZone);
  }
}
