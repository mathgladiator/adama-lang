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
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;

/** a reactive data type to hide and hold an entire json tree */
public class RxDynamic extends RxBase implements Comparable<RxDynamic>, CanGetAndSet<NtDynamic> {
  private NtDynamic backup;
  private NtDynamic value;

  public RxDynamic(final RxParent parent, final NtDynamic value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      reverseDelta.writeObjectFieldIntro(name);
      Object from = new JsonStreamReader(backup.json).readJavaTree();
      Object to = new JsonStreamReader(value.json).readJavaTree();
      JsonAlgebra.writeObjectFieldDelta(from, to, forwardDelta);
      JsonAlgebra.writeObjectFieldDelta(to, from, reverseDelta);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.injectJson(value.json);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = new NtDynamic(reader.skipValueIntoJson());
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtDynamic());
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
  public int compareTo(final RxDynamic other) {
    return value.compareTo(other.value);
  }

  @Override
  public NtDynamic get() {
    return value;
  }

  @Override
  public void set(final NtDynamic value) {
    if (!this.value.equals(value)) {
      this.value = value;
      __raiseDirty();
    }
  }
}
