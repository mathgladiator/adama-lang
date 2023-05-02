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

@FunctionalInterface
public interface ExceptionSupplier<T> {

  static <T> Supplier<T> TO_RUNTIME(ExceptionSupplier<T> supplier) {
    return () -> {
      try {
        return supplier.get();
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    };
  }

  T get() throws Exception;
}
