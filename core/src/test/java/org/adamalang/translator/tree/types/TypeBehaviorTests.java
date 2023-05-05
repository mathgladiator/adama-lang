package org.adamalang.translator.tree.types;

import org.junit.Assert;
import org.junit.Test;

public class TypeBehaviorTests {
  @Test
  public void trivial() {
    Assert.assertEquals("ReadOnlyNativeValue", TypeBehavior.ReadOnlyNativeValue.name());
  }
}
