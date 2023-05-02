/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.validators;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class ValidateSpaceInfoTests {
  @Test
  public void tooLong() throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append("xyz");
    for (int k = 3; k < 127; k++) {
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
    try {
      ValidateSpace.validate("ab");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(904364, ece.code);
    }
  }

  @Test
  public void tooComplex() {
    try {
      ValidateSpace.validate("#&sds");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(998515, ece.code);
    }
  }

  @Test
  public void doubleHyphen() {
    try {
      ValidateSpace.validate("abc-x-def");
    } catch (ErrorCodeException ece) {
      Assert.fail();
    }
    try {
      ValidateSpace.validate("abc--def");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(950465, ece.code);
    }
  }

  @Test
  public void good() throws Exception {
    ValidateSpace.validate("simple");
  }

  @Test
  public void inappropriateNamesDueToBadActors() {
    try {
      ValidateSpace.validate("api");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(904364, ece.code);
    }
    try {
      ValidateSpace.validate("CSS");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(904364, ece.code);
    }
  }

  @Test
  public void tooShort2() {
    try {
      ValidateSpace.validate("..");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(904364, ece.code);
    }
    try {
      ValidateSpace.validate("d");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(904364, ece.code);
    }
  }

  @Test
  public void invalidCharacters() {
    try {
      ValidateSpace.validate("my-domain.com");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(950465, ece.code);
    }
    try {
      ValidateSpace.validate(".aws");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(950465, ece.code);
    }
    try {
      ValidateSpace.validate("_aws");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(950465, ece.code);
    }
    try {
      ValidateSpace.validate(".git");
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(950465, ece.code);
    }
  }
}
