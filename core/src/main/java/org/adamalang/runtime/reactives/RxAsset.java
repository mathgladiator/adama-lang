/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtAsset;

/** a reactive asset type */
public class RxAsset extends RxBase implements Comparable<RxAsset>, CanGetAndSet<NtAsset> {
  private NtAsset backup;
  private NtAsset value;

  public RxAsset(final RxParent parent, final NtAsset value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.writeNtAsset(value);
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.writeNtAsset(backup);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtAsset(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtAsset();
    value = backup;
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    set(reader.readNtAsset());
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      value = backup;
      __lowerDirtyRevert();
    }
  }

  @Override
  public int compareTo(final RxAsset other) {
    return value.compareTo(other.value);
  }

  @Override
  public NtAsset get() {
    return value;
  }

  @Override
  public void set(final NtAsset value) {
    if (!this.value.equals(value)) {
      this.value = value;
      __raiseDirty();
    }
  }
}
