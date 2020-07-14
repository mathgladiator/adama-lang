/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.Indexable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.natives.NtClient;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
  public void __commit(final String name, final ObjectNode delta) {
    if (__isDirty()) {
      value.dump(delta.putObject(name));
      backup = value;
      __lowerDirtyCommit();
    }
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
