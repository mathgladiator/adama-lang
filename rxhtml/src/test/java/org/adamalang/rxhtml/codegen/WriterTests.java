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
