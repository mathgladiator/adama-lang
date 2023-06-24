/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
