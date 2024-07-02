/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
      Assert.fail();
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
