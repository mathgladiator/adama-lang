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
import org.adamalang.runtime.natives.NtDate;

/** A single date in the typical gregorian calendar */
public class RxDate extends RxIndexableBase implements Comparable<RxDate>, CanGetAndSet<NtDate> {
  private NtDate backup;
  private NtDate value;

  public RxDate(final RxParent parent, final NtDate value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeNtDate(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeNtDate(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtDate(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtDate();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtDate());
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
    return super.__memory() + backup.memory() + value.memory() + 16;
  }

  @Override
  public NtDate get() {
    return value;
  }

  @Override
  public void set(final NtDate value) {
    trigger();
    this.value = value;
    trigger();
    __raiseDirty();
  }

  @Override
  public int compareTo(RxDate o) {
    return value.compareTo(o.value);
  }

  @Override
  public int getIndexValue() {
    return value.toInt();
  }
}
