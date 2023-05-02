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
import org.adamalang.runtime.natives.NtDateTime;

/** a reactive date and a time with the time zone in the typical gregorian calendar */
public class RxDateTime extends RxBase implements CanGetAndSet<NtDateTime>, Comparable<RxDateTime> {
  private NtDateTime backup;
  private NtDateTime value;

  public RxDateTime(final RxParent parent, final NtDateTime value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeNtDateTime(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeNtDateTime(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtDateTime(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtDateTime();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtDateTime());
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
  public NtDateTime get() {
    return value;
  }

  @Override
  public void set(final NtDateTime value) {
    this.value = value;
    __raiseDirty();
  }

  @Override
  public int compareTo(RxDateTime o) {
    return value.compareTo(o.value);
  }
}
