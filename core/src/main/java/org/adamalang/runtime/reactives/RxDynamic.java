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
