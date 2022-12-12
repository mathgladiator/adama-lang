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
