/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionSupplierTests {
  @Test
  public void coverage() {
    ExceptionSupplier<String> supplier = new ExceptionSupplier<String>() {
      @Override
      public String get() throws Exception {
        throw new Exception();
      }
    };

    try {
      ExceptionSupplier.TO_RUNTIME(supplier).get();
      Assert.fail();
    } catch (RuntimeException re) {
    }
  }
}
