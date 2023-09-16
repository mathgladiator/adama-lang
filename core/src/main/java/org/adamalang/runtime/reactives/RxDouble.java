/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
