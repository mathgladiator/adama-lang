package org.adamalang.validators;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class ValidateChannelTests {
  @Test
  public void empty() {
    try {
      ValidateChannel.validate("");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(950399, ece.code);
    }
  }

  @Test
  public void badstart() {
    try {
      ValidateChannel.validate("-");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(908415, ece.code);
    }
  }

  @Test
  public void badmiddle() {
    try {
      ValidateChannel.validate("a-");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(967804, ece.code);
    }
  }

  @Test
  public void good() throws Exception{
    ValidateChannel.validate("fooninja");
  }
}
