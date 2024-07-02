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

/** a reactive string */
public class RxString extends RxIndexableBase implements Comparable<RxString>, CanGetAndSet<String> {
  protected String backup;
  protected String value;

  public RxString(final RxParent owner, final String value) {
    super(owner);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeString(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeString(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeString(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readString();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readString());
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
    return super.__memory() + (backup.length() + value.length()) * 2L + 16;
  }

  @Override
  public int compareTo(final RxString other) {
    return value.compareTo(other.value);
  }

  public int compareToIgnoreCase(final RxString other) {
    return value.compareToIgnoreCase(other.value);
  }

  @Override
  public String get() {
    return value;
  }

  @Override
  public void set(final String value) {
    if (this.value != null && this.value.equals(value)) {
      return;
    }
    trigger();
    this.value = value;
    trigger();
    __raiseDirty();
  }


  @Override
  public int getIndexValue() {
    return value.hashCode();
  }

  public boolean has() {
    return !value.isEmpty();
  }

  public String opAddTo(final Object incoming) {
    trigger();
    value += incoming.toString();
    trigger();
    __raiseDirty();
    return value;
  }
}
