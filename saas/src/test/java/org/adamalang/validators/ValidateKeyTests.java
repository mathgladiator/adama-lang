package org.adamalang.validators;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class ValidateKeyTests {
  @Test
  public void tooLong() throws Exception {
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < 511; k++) {
      sb.append("a");
      ValidateKey.validate(sb.toString());
    }
    try {
      sb.append("a");
      ValidateKey.validate(sb.toString());
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(946192, ece.code);
    }
  }

  @Test
  public void tooShort() {
    try {
      ValidateKey.validate("");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(919676, ece.code);
    }
  }

  @Test
  public void tooComplex() {
    try {
      ValidateKey.validate("#&");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(946192, ece.code);
    }
  }

  @Test
  public void good() throws Exception {
    ValidateKey.validate("simple");
  }
}
