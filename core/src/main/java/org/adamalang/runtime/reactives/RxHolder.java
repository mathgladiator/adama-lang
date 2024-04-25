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
import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMessageBase;

import java.util.function.Supplier;

public class RxHolder<Ty extends NtMessageBase> extends RxBase implements CanGetAndSet<Ty> {
  private final Supplier<Ty> maker;
  private Ty backup;
  private Ty value;

  public RxHolder(final RxParent parent, Supplier<Ty> maker) {
    super(parent);
    backup = maker.get();
    this.value = backup;
    this.maker = maker;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      reverseDelta.writeObjectFieldIntro(name);
      Object from = new JsonStreamReader(backup.to_dynamic().json).readJavaTree();
      Object to = new JsonStreamReader(value.to_dynamic().json).readJavaTree();
      JsonAlgebra.writeObjectFieldDelta(from, to, forwardDelta);
      JsonAlgebra.writeObjectFieldDelta(to, from, reverseDelta);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.injectJson(value.to_dynamic().json);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = maker.get();
    backup.__ingest(reader);
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    value.__ingest(reader);
    __raiseDirty();
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
    return super.__memory() + backup.__memory() + value.__memory() + 16;
  }

  @Override
  public Ty get() {
    return value;
  }

  public Ty write() {
    __raiseDirty();
    return value;
  }

  @Override
  public void set(final Ty value) {
    this.value = value;
    __raiseDirty();
  }
}
