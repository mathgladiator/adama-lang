/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtTimeSpan;

/** a reactive time span measured in seconds */
public class RxTimeSpan extends RxBase implements CanGetAndSet<NtTimeSpan>, Comparable<RxTimeSpan> {
  private NtTimeSpan backup;
  private NtTimeSpan value;

  public RxTimeSpan(final RxParent parent, final NtTimeSpan value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeNtTimeSpan(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeNtTimeSpan(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtTimeSpan(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtTimeSpan();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtTimeSpan());
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
  public NtTimeSpan get() {
    return value;
  }

  @Override
  public void set(final NtTimeSpan value) {
    this.value = value;
    __raiseDirty();
  }

  @Override
  public int compareTo(RxTimeSpan o) {
    return value.compareTo(o.value);
  }
}
