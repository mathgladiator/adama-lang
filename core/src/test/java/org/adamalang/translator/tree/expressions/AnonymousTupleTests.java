package org.adamalang.translator.tree.expressions;

import org.junit.Assert;
import org.junit.Test;

public class AnonymousTupleTests {
  @Test
  public void labels() {
    String[] label = new String[] { "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth", "tenth", "pos_11", "pos_12"};
    for (int k = 0; k < label.length; k++) {
      Assert.assertEquals(label[k], AnonymousTuple.nameOf(k));
    }
  }
}
