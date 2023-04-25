/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

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
    return year + "/" + month + "/" + day;
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
}
