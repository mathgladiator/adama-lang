/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.mocks;

import java.util.function.Consumer;
import org.adamalang.runtime.contracts.CanGetAndSet;

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
