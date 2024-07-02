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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class ErrorCodeExceptionTests {
  @Test
  public void coverage() {
    ExceptionLogger logger = (t, errorCode) -> {
    };
    ErrorCodeException ex1 = new ErrorCodeException(42);
    ErrorCodeException ex2 = new ErrorCodeException(500, new NullPointerException());
    ErrorCodeException ex3 = new ErrorCodeException(4242, "nope");
    Assert.assertEquals(42, ErrorCodeException.detectOrWrap(100, ex1, logger).code);
    Assert.assertEquals(500, ErrorCodeException.detectOrWrap(100, new RuntimeException(ex2), logger).code);
    Assert.assertEquals(100, ErrorCodeException.detectOrWrap(100, new NullPointerException(), logger).code);
    Assert.assertEquals(42, ex1.code);
    Assert.assertEquals(500, ex2.code);
    Assert.assertEquals(4242, ex3.code);
  }
}
