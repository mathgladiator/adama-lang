package org.adamalang.api.commands;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashSet;

public class ErrorCodesTests {
  @Test
  public void sanity() {
    new ErrorCodes();
  }

  @Test
  public void no_dupes() throws Exception {
    HashSet<Integer> values = new HashSet<>();
    for (Field field: org.adamalang.runtime.ErrorCodes.class.getFields()) {
      int v = (int) field.get(null);
      Assert.assertFalse("dupe:" + v, values.contains(v));
    }
  }
}
