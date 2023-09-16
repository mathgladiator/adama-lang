/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
