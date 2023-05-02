/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
}
