package org.adamalang.rxhtml.codegen;

import org.junit.Assert;
import org.junit.Test;

public class VariablePoolTests {
  @Test
  public void flow() {
    VariablePool pool = new VariablePool();
    String a = pool.ask();
    Assert.assertEquals("a", a);
    String b = pool.ask();
    Assert.assertEquals("b", b);
    String c = pool.ask();
    Assert.assertEquals("c", c);
    pool.give(a);
    Assert.assertEquals("a", pool.ask());
  }
}
