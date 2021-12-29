/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.natives;

/** a native data type to hide and hold an entire json tree */
public class NtDynamic implements Comparable<NtDynamic>  {
  public final String json;
  public NtDynamic(String json) {
    this.json = json;
  }

  @Override
  public int compareTo(final NtDynamic other) {
    return json.compareTo(other.json);
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof NtDynamic) { return ((NtDynamic) o).json.equals(json); }
    return false;
  }

  @Override
  public int hashCode() {
    return json.hashCode();
  }

  @Override
  public String toString() {
    return json;
  }

  public static final NtDynamic NULL = new NtDynamic("null");

  public long memory() {
    return json.length() * 2;
  }
}
