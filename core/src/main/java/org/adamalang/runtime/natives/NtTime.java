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
package org.adamalang.runtime.natives;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/** a time within a day at the precision of a minute */
public class NtTime implements Comparable<NtTime> {
  public final int hour;
  public final int minute;

  public NtTime(int hour, int minute) {
    this.hour = hour;
    this.minute = minute;
  }

  @Override
  public int hashCode() {
    return Objects.hash(hour, minute);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NtTime ntTime = (NtTime) o;
    return hour == ntTime.hour && minute == ntTime.minute;
  }

  @Override
  public String toString() {
    return (hour < 10 ? ("0" + hour) : hour) + (minute < 10 ? ":0" : ":") + minute;
  }

  public long memory() {
    return 24;
  }

  public int toInt() {
    return hour * 60 + minute;
  }

  @Override
  public int compareTo(NtTime o) {
    if (hour < o.hour) {
      return -1;
    } else if (hour > o.hour) {
      return 1;
    }
    return Integer.compare(minute, o.minute);
  }

  public static NtTime parse(String val) {
    try {
      String[] parts = val.split(Pattern.quote(":"));
      return new NtTime(Integer.parseInt(parts[0]), parts.length > 1 ? Integer.parseInt(parts[1]) : 0);
    } catch (Exception nfe) {
      return new NtTime(0, 0);
    }
  }
}
