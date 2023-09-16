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

import java.time.ZonedDateTime;
import java.util.Objects;

/** a date and a time with the time zone in the typical gregorian calendar */
public class NtDateTime implements Comparable<NtDateTime> {
  public final ZonedDateTime dateTime;

  public NtDateTime(ZonedDateTime dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  public int hashCode() {
    return Objects.hash(dateTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NtDateTime that = (NtDateTime) o;
    return Objects.equals(dateTime, that.dateTime);
  }

  @Override
  public String toString() {
    return dateTime.toString();
  }

  public long memory() {
    return 64;
  }

  @Override
  public int compareTo(NtDateTime o) {
    return dateTime.compareTo(o.dateTime);
  }

  public int toInt() {
    long val = dateTime.toInstant().toEpochMilli();
    // convert to minutes since epoch as this fits within int
    return (int) (val / 60000L);
  }
}
