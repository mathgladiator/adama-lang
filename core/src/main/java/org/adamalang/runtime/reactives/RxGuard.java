/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/**
 * a condition to learn if changes have occured. This is like a Lazy, but gives people the ability
 * to learn if changes have happened since the last time a commited happened
 */
public class RxGuard extends RxBase implements RxChild {
  boolean invalid;
  private int generation;

  public RxGuard(RxParent parent) {
    super(null);
    generation = 1;
    invalid = true;
    if (parent instanceof RxRecordBase) {
      generation = ((RxRecordBase) parent).__id();
    }
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

  private void inc() {
    generation *= 65521;
    generation ++;
  }

  @Override
  public void __revert() {
    if (invalid) {
      inc();
      invalid = false;
    }
  }

  @Override
  public boolean __raiseInvalid() {
    inc();
    invalid = true;
    return true;
  }

  public int getGeneration() {
    return generation;
  }
}
