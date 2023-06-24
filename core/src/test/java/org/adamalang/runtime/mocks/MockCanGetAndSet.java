/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
