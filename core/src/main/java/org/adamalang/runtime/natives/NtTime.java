/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives;

import java.util.Objects;

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
    return hour + (minute < 10 ? ":0" : ":") + minute;
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
}
