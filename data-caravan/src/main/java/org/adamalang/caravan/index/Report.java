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
package org.adamalang.caravan.index;

/** a report of free space */
public class Report {
  private long total;
  private long free;

  /** add total bytes allocated to the report */
  public void addTotal(long total) {
    this.total += total;
  }

  /** report some free bytes available */
  public void addFree(long free) {
    this.free += free;
  }

  /** get the total bytes allocated to the storage */
  public long getTotalBytes() {
    return total;
  }

  /** get the total bytes free to the storage system */
  public long getFreeBytesAvailable() {
    return free;
  }

  /** return true if the free is below the given threshold (i.e. 0.2) */
  public boolean alarm(double ratio) {
    return free < ((long) total * ratio);
  }
}
