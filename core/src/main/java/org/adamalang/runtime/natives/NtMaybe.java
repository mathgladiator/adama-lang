/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

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

  /** get the value; note; this returns null and is not appropriate for the runtime */
  public T get() {
    return this.value;
  }

  /**
   * get the value if it is available, otherwise return the default value (appropriate for runtime)
   */
  public T getOrDefaultTo(T defaultValue) {
    if (this.value != null) {
      return this.value;
    }
    return defaultValue;
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

  public <O> NtMaybe<O> unpack(Function<T, O> func) {
    if (value == null) {
      return new NtMaybe<>();
    } else {
      return new NtMaybe<>(func.apply(value));
    }
  }

  public <O> NtMaybe<O> unpackTransfer(Function<T, NtMaybe<O>> func) {
    if (value == null) {
      return new NtMaybe<>();
    } else {
      return func.apply(value);
    }
  }

  @Override
  public String toString() {
    if (value != null) {
      return value.toString();
    } else {
      return "";
    }
  }
}
