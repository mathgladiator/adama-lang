/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import java.util.Comparator;
import java.util.function.Consumer;
import org.adamalang.runtime.reactives.RxMaybe;

/** a maybe for a boxed java type (i.e. Integer) */
public class NtMaybe<T> {
  private Consumer<T> assignChain;
  private Runnable deleteChain;
  private T value;

  /** construct without a value */
  public NtMaybe() {
    this.value = null;
    this.deleteChain = null;
  }

  public NtMaybe(final NtMaybe<T> other) {
    this.value = null;
    if (other != null) {
      this.value = other.value;
    }
    this.deleteChain = null;
  }

  /** construct with a given value */
  public NtMaybe(final T value) {
    this.value = value;
    this.deleteChain = null;
  }

  public int compareValues(final NtMaybe<T> other, final Comparator<T> test) {
    if (value == null && other.value == null) {
      return 0;
    } else if (value == null) {
      return 1;
    } else if (other.value == null) {
      return -1;
    } else {
      return test.compare(value, other.value);
    }
  }

  public void delete() {
    this.value = null;
    if (deleteChain != null) {
      deleteChain.run();
    }
    if (assignChain != null) {
      assignChain.accept(this.value);
    }
  }

  public T get() {
    return this.value;
  }

  /** is it available */
  public boolean has() {
    return value != null;
  }

  public NtMaybe<T> resolve() {
    return this;
  }

  /** copy the value from another maybe */
  public void set(final NtMaybe<T> value) {
    this.value = value.value;
    if (assignChain != null) {
      assignChain.accept(this.value);
    }
  }

  /** set the value */
  public void set(final T value) {
    this.value = value;
    if (assignChain != null) {
      assignChain.accept(this.value);
    }
  }

  public NtMaybe<T> withAssignChain(final Consumer<T> assignChain) {
    this.assignChain = assignChain;
    return this;
  }

  public NtMaybe<T> withDeleteChain(final Runnable deleteChain) {
    this.deleteChain = deleteChain;
    return this;
  }
}
