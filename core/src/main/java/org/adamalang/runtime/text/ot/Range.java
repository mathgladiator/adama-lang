/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.text.ot;

/** a subrange of another operand */
public class Range implements Operand {

  public final Operand base;
  public final int at;
  public final int length;

  public Range(Operand base, int at, int length) {
    this.base = base;
    this.at = at;
    this.length = length;
  }

  @Override
  public void transposeRangeIntoJoin(int at, int length, Join join) {
    if (at == 0 && this.length == length) {
      join.children.add(this);
    } else {
      join.children.add(new Range(base, this.at + at, length));
    }
  }

  @Override
  public String get() {
    return base.get().substring(at, at + length);
  }

  @Override
  public int length() {
    return length;
  }
}
