/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
  public void __commit(final String name, final JsonStreamWriter writer) {
    if (__isDirty()) {
      writer.writeObjectFieldIntro(name);
      writer.writeDouble(value);
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
  public void __revert() {
    if (__isDirty()) {
      value = backup;
      __lowerDirtyRevert();
    }
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

  public double opAddTo(final double incoming) {
    value += incoming;
    __raiseDirty();
    return value;
  }

  public double opDivBy(final double x) {
    value /= x;
    __raiseDirty();
    return value;
  }

  public double opMultBy(final double x) {
    value *= x;
    __raiseDirty();
    return value;
  }

  public double opSubFrom(final double x) {
    value -= x;
    __raiseDirty();
    return value;
  }

  public void set(final double value) {
    this.value = value;
    __raiseDirty();
  }

  @Override
  public void set(final Double value) {
    set(value.doubleValue());
  }

  public void set(final int value) {
    this.value = value;
    __raiseDirty();
  }
}
