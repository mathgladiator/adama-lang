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

import java.time.LocalDate;
import java.util.Objects;

/** A single date in the typical gregorian calendar */
public class NtDate implements Comparable<NtDate> {
  public final int year;
  public final int month;
  public final int day;

  public NtDate(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  @Override
  public int hashCode() {
    return Objects.hash(year, month, day);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NtDate ntDate = (NtDate) o;
    return year == ntDate.year && month == ntDate.month && day == ntDate.day;
  }

  @Override
  public String toString() {
    return year + (month < 10 ? "-0" : "-") + month + (day < 10 ? "-0" : "-") + day;
  }

  public long memory() {
    return 24;
  }

  @Override
  public int compareTo(NtDate o) {
    if (year < o.year) {
      return -1;
    } else if (year > o.year) {
      return 1;
    }
    if (month < o.month) {
      return -1;
    } else if (month > o.month) {
      return 1;
    }
    if (day < o.day) {
      return -1;
    } else if (day > o.day) {
      return 1;
    }
    return 0;
  }

  public int toInt() {
    return year * (366 * 32) + month * 32 + day;
  }

  public LocalDate toLocalDate() {
    return LocalDate.of(year, month, day);
  }
}
