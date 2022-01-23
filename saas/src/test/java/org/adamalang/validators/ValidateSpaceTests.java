package org.adamalang.validators;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class ValidateSpaceTests {
  @Test
  public void tooLong() throws Exception {
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < 127; k++) {
      sb.append("a");
      ValidateSpace.validate(sb.toString());
    }
    try {
      sb.append("a");
      ValidateSpace.validate(sb.toString());
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(998515, ece.code);
    }
  }

  @Test
  public void tooShort() {
    try {
      ValidateSpace.validate("");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(937076, ece.code);
    }
  }

  @Test
  public void tooComplex() {
    try {
      ValidateSpace.validate("#&");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(998515, ece.code);
    }
  }

  @Test
  public void good() throws Exception {
    ValidateSpace.validate("simple");
  }
}
