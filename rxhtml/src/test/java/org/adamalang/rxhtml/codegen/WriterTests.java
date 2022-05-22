/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml.codegen;

import org.junit.Assert;
import org.junit.Test;

public class WriterTests {
  @Test
  public void flow() {
    Writer writer = new Writer();
    writer.append("x").tab().tabUp().append("y").tab().tabDown().append("z").tabDown().append("!").newline().append("!");
    Assert.assertEquals("xy  z!\n!", writer.toString());
  }
}
