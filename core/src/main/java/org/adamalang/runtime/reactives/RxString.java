/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.RxParent;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a reactive string */
public class RxString extends RxBase implements Comparable<RxString>, CanGetAndSet<String> {
  private String backup;
  private String value;

  public RxString(final RxParent owner, final String value) {
    super(owner);
    backup = value;
    this.value = value;
  }

  @Override
  public void __commit(final String name, final ObjectNode delta) {
    if (__isDirty()) {
      delta.put(name, value);
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
  public int compareTo(final RxString other) {
    return value.compareTo(other.value);
  }

  @Override
  public String get() {
    return value;
  }

  public boolean has() {
    return !value.isEmpty();
  }

  public String opAddTo(final boolean incoming) {
    value += incoming;
    __raiseDirty();
    return value;
  }

  public String opAddTo(final double incoming) {
    value += incoming;
    __raiseDirty();
    return value;
  }

  public String opAddTo(final int incoming) {
    value += incoming;
    __raiseDirty();
    return value;
  }

  public String opAddTo(final String incoming) {
    value += incoming;
    __raiseDirty();
    return value;
  }

  @Override
  public void set(final String value) {
    this.value = value;
    __raiseDirty();
  }
}
