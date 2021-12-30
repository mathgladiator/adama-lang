package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class ValidatorsTests {
  @Test
  public void flow() {
    Assert.assertFalse(Validators.simple("1234", 3));
    Assert.assertFalse(Validators.simple("$!@", 43));
    Assert.assertTrue(Validators.simple("x1234", 1000));
    Assert.assertTrue(Validators.simple("1234", 1000));
    Assert.assertTrue(Validators.simple("ninja-cake", 1000));
  }
}
