/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.Indexable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;

/** a reactive client */
public class RxClient extends RxBase implements Comparable<RxClient>, CanGetAndSet<NtClient>, Indexable {
  private NtClient backup;
  private NtClient value;

  public RxClient(final RxParent parent, final NtClient value) {
    super(parent);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(final String name, final JsonStreamWriter writer) {
    if (__isDirty()) {
      writer.writeObjectFieldIntro(name);
      writer.writeNtClient(value);
      backup = value;
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.writeNtClient(value);
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    backup = reader.readNtClient();
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
  public int compareTo(final RxClient other) {
    return value.compareTo(other.value);
  }

  @Override
  public NtClient get() {
    return value;
  }

  @Override
  public int getIndexValue() {
    return value.hashCode();
  }

  @Override
  public void set(final NtClient value) {
    if (!this.value.equals(value)) {
      this.value = value;
      __raiseDirty();
    }
  }
}
