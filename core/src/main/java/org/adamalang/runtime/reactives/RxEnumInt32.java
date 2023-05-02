/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;

import java.util.function.Function;

/** a reactive 32-bit integer (int) used by enums with the ability to correct invalid values */
public class RxEnumInt32 extends RxInt32 {
  private final Function<Integer, Integer> fixer;

  public RxEnumInt32(RxParent parent, int value, Function<Integer, Integer> fixer) {
    super(parent, value);
    this.fixer = fixer;
  }

  @Override
  public void __insert(JsonStreamReader reader) {
    super.__insert(reader);
    this.backup = fixer.apply(this.backup);
    this.value = fixer.apply(this.value);
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readInteger());
  }

  @Override
  public void forceSet(int id) {
    super.forceSet(fixer.apply(id));
  }

  @Override
  public void set(Integer value) {
    super.set(fixer.apply(value));
  }
}
