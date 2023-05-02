/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.text;

/** a string combined with a sequencer */
public class SeqString {
  public final int seq;
  public final String value;

  public SeqString(int seq, String value) {
    this.seq = seq;
    this.value = value;
  }
}
