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

import org.adamalang.runtime.json.JsonStreamReader;

/** a native data type to hide and hold an entire json tree */
public class NtDynamic implements Comparable<NtDynamic> {
  public static final NtDynamic NULL = new NtDynamic("null");
  public final String json;
  private Object cached;

  public NtDynamic(String json) {
    this.json = json;
    this.cached = null;
  }

  public Object cached() {
    if (cached != null) {
      return cached;
    }
    cached = new JsonStreamReader(json).readJavaTree();
    return cached;
  }

  @Override
  public int compareTo(final NtDynamic other) {
    return json.compareTo(other.json);
  }

  @Override
  public int hashCode() {
    return json.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof NtDynamic) {
      return ((NtDynamic) o).json.equals(json);
    }
    return false;
  }

  @Override
  public String toString() {
    return json;
  }

  public long memory() {
    return json.length() * 2L;
  }
}
