/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.CanGetAndSet;

import java.util.function.Consumer;

public class MockCanGetAndSet<T> implements CanGetAndSet<T>, Consumer<T> {
  private T value;

  public MockCanGetAndSet() {
    value = null;
  }

  @Override
  public void accept(final T t) {
    this.value = t;
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public void set(final T value) {
    this.value = value;
  }
}
