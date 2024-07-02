/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

/** a reactive 64-bit integer (long) */
public class RxInt64 extends RxIndexableBase implements Comparable<RxInt64>, CanGetAndSet<Long> {
  private long backup;
  private long value;

  public RxInt64(final RxParent owner, final long value) {
    super(owner);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeLong(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeLong(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeLong(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readLong();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readLong());
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

  public long bumpDownPost() {
    trigger();
    final var result = value--;
    trigger();
    __raiseDirty();
    return result;
  }

  public long bumpDownPre() {
    trigger();
    final var result = --value;
    trigger();
    __raiseDirty();
    return result;
  }

  public long bumpUpPost() {
    trigger();
    final var result = value++;
    trigger();
    __raiseDirty();
    return result;
  }

  // these make ZERO sense
  public long bumpUpPre() {
    trigger();
    final var result = ++value;
    trigger();
    __raiseDirty();
    return result;
  }

  @Override
  public int compareTo(final RxInt64 other) {
    return Long.compare(value, other.value);
  }

  @Override
  public Long get() {
    return value;
  }

  @Override
  public void set(final Long value) {
    if (this.value != value) {
      trigger();
      this.value = value;
      trigger();
      __raiseDirty();
    }
  }

  @Override
  public int getIndexValue() {
    return (int) value;
  }

  public long opAddTo(final long incoming) {
    if (incoming != 0) {
      trigger();
      value += incoming;
      trigger();
      __raiseDirty();
    }
    return value;
  }

  public long opMultBy(final long x) {
    if (x != 1) {
      value *= x;
      __raiseDirty();
    }
    return value;
  }

  public void set(final int value) {
    if (this.value != value) {
      trigger();
      this.value = value;
      trigger();
      __raiseDirty();
    }
  }
}
