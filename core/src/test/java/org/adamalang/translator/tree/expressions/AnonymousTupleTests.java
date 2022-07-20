/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
