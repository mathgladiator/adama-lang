/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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
