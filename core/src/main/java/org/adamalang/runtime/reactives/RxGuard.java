/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/** a condition to learn if changes have occured. This is like a Lazy, but gives
 * people the ability to learn if changes have happened since the last time a
 * commited happened */
public class RxGuard extends RxBase implements RxChild {
  private int generation;
  boolean invalid;

  public RxGuard() {
    super(null);
    generation = 1;
    invalid = true;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    __revert();
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
  }

  @Override
  public void __patch(JsonStreamReader reader) {
  }

  @Override
  public boolean __raiseInvalid() {
    generation++;
    invalid = true;
    return true;
  }

  @Override
  public void __revert() {
    if (invalid) {
      generation++;
      invalid = false;
    }
  }

  public int getGeneration() {
    return generation;
  }
}
