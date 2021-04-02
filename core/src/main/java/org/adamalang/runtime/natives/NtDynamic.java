/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
}
