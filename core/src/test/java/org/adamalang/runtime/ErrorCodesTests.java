package org.adamalang.runtime;

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
    for (Field field: ErrorCodes.class.getFields()) {
      int v = (int) field.get(null);
      Assert.assertFalse("dupe:" + v, values.contains(v));
    }
  }
}
