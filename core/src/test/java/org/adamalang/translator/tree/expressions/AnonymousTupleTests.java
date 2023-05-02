/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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
