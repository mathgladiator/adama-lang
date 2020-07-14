/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.CanGetAndSet;
import org.adamalang.runtime.contracts.Indexable;
import org.adamalang.runtime.contracts.RxParent;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a reactive 32-bit integer (int) */
public class RxInt32 extends RxBase implements Comparable<RxInt32>, CanGetAndSet<Integer>, Indexable {
  private int backup;
  private int value;

  public RxInt32(final RxParent parent, final int value) {
    super(parent);
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

  public int bumpDownPost() {
    final var result = value--;
    __raiseDirty();
    return result;
  }

  public int bumpDownPre() {
    final var result = --value;
    __raiseDirty();
    return result;
  }

  public int bumpUpPost() {
    final var result = value++;
    __raiseDirty();
    return result;
  }

  // these make ZERO sense
  public int bumpUpPre() {
    final var result = ++value;
    __raiseDirty();
    return result;
  }

  @Override
  public int compareTo(final RxInt32 other) {
    return Integer.compare(value, other.value);
  }

  @Override
  public Integer get() {
    return value;
  }

  @Override
  public int getIndexValue() {
    return value;
  }

  public int opAddTo(final int incoming) {
    value += incoming;
    __raiseDirty();
    return value;
  }

  public int opModBy(final int x) {
    value %= x;
    __raiseDirty();
    return value;
  }

  public int opMultBy(final int x) {
    value *= x;
    __raiseDirty();
    return value;
  }

  public int opSubFrom(final int x) {
    value -= x;
    __raiseDirty();
    return value;
  }

  @Override
  public void set(final Integer value) {
    if (this.value != value) {
      this.value = value;
      __raiseDirty();
    }
  }
}
