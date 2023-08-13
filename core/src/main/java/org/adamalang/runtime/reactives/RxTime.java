/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.Indexable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtTime;

/** a reactive time within a day at the precision of a minute */
public class RxTime extends RxBase implements CanGetAndSet<NtTime>, Comparable<RxTime>, Indexable {
  private NtTime backup;
  private NtTime value;

  public RxTime(final RxParent parent, final NtTime value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeNtTime(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeNtTime(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtTime(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtTime();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtTime());
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
  public NtTime get() {
    return value;
  }

  @Override
  public void set(final NtTime value) {
    this.value = value;
    __raiseDirty();
  }

  @Override
  public int compareTo(RxTime o) {
    return value.compareTo(o.value);
  }

  @Override
  public int getIndexValue() {
    return value.toInt();
  }
}
