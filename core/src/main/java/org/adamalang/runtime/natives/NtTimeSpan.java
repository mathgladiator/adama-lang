/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
