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
package org.adamalang.runtime.natives;

import java.util.Objects;

/** a time span measured in seconds */
public class NtTimeSpan implements Comparable<NtTimeSpan> {
  public final double seconds;

  public NtTimeSpan(double seconds) {
    this.seconds = seconds;
  }

  @Override
  public int hashCode() {
    return Objects.hash(seconds);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NtTimeSpan that = (NtTimeSpan) o;
    return Double.compare(that.seconds, seconds) == 0;
  }

  @Override
  public String toString() {
    return seconds + " sec";
  }

  public long memory() {
    return 24;
  }

  @Override
  public int compareTo(NtTimeSpan o) {
    return Double.compare(seconds, o.seconds);
  }
}
