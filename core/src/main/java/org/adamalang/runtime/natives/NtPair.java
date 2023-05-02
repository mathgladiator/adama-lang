/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

/** a pairing within a map, and a stand alone value */
public class NtPair<D, R> {
  public final D key;
  public final R value;

  public NtPair(D key, R value) {
    this.key = key;
    this.value = value;
  }

  public NtPair(NtPair<D, R> other) {
    this.key = other.key;
    this.value = other.value;
  }
}
