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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/** the various rules for cron jobs */
public class CronChecker {
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
}
