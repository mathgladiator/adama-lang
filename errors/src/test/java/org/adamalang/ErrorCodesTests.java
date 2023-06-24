/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashSet;

public class ErrorCodesTests {
  @Test
  public void coverage() {
    new ErrorCodes();
  }

  @Test
  public void no_dupes() throws Exception {
    HashSet<Integer> values = new HashSet<>();
    for (Field field : ErrorCodes.class.getFields()) {
      int v = (int) field.get(null);
      Assert.assertFalse("dupe:" + v, values.contains(v));
      values.add(v);
    }
  }
}
