/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import java.util.function.Supplier;

/** Helper for getting something exactly once */
public class Once<T> {
  private T cache;

  public Once() {
    this.cache = null;
  }

  public T access(Supplier<T> supplier) {
    if (cache == null) {
      cache = supplier.get();
    }
    return cache;
  }
}
