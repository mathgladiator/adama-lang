/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/** a reactive boolean */
public class RxBoolean extends RxBase implements Comparable<RxBoolean>, CanGetAndSet<Boolean> {
  private boolean backup;
  private boolean value;

  public RxBoolean(final RxParent owner, final boolean value) {
    super(owner);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeBoolean(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeBoolean(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeBoolean(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readBoolean();
    value = backup;
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      value = backup;
      __lowerDirtyRevert();
    }
  }

  @Override
  public int compareTo(final RxBoolean other) {
    return Boolean.compare(value, other.value);
  }

  @Override
  public Boolean get() {
    return value;
  }

  @Override
  public void set(final Boolean value) {
    if (this.value != value) {
      this.value = value;
      __raiseDirty();
    }
  }
}
