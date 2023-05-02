/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/** a reactive double */
public class RxDouble extends RxBase implements Comparable<RxDouble>, CanGetAndSet<Double> {
  private double backup;
  private double value;

  public RxDouble(final RxParent parent, final double value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeDouble(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeDouble(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeDouble(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readDouble();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readDouble());
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      value = backup;
      __lowerDirtyRevert();
    }
  }

  @Override
  public long __memory() {
    return super.__memory() + 16;
  }

  public void set(final double value) {
    this.value = value;
    __raiseDirty();
  }

  public double bumpDownPost() {
    final var result = value--;
    __raiseDirty();
    return result;
  }

  public double bumpDownPre() {
    final var result = --value;
    __raiseDirty();
    return result;
  }

  public double bumpUpPost() {
    final var result = value++;
    __raiseDirty();
    return result;
  }

  public double bumpUpPre() {
    final var result = ++value;
    __raiseDirty();
    return result;
  }

  @Override
  public int compareTo(final RxDouble other) {
    return Double.compare(value, other.value);
  }

  @Override
  public Double get() {
    return value;
  }

  @Override
  public void set(final Double value) {
    set(value.doubleValue());
  }

  public double opAddTo(final double incoming) {
    value += incoming;
    __raiseDirty();
    return value;
  }

  public double opMultBy(final double x) {
    value *= x;
    __raiseDirty();
    return value;
  }

  public void set(final int value) {
    this.value = value;
    __raiseDirty();
  }
}
