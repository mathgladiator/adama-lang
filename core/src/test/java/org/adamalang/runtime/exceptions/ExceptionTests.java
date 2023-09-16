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
package org.adamalang.runtime.exceptions;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class ExceptionTests {
  @Test
  public void detectOrWrap() {
    final var gwee = new GoodwillExhaustedException(0, 1, 2, 3);
    Assert.assertEquals("Good will exhausted:0,1 --> 2,3", gwee.getMessage());
    ErrorCodeException eee = new ErrorCodeException(14, "Nope");
    Assert.assertTrue(
        eee
            == ErrorCodeException.detectOrWrap(
                5400,
                eee,
                new ExceptionLogger() {
                  @Override
                  public void convertedToErrorCode(Throwable t, int errorCode) {
                    t.printStackTrace();
                  }
                }));
  }

  @Test
  public void cons() {
    new AbortMessageException();
    new RetryProgressException(null);
    new ComputeBlockedException(NtPrincipal.NO_ONE, "foo");
    new ComputeBlockedException();
    new ErrorCodeException(14, "Nope");
    new PerformDocumentRewindException(100);
    new PerformDocumentDeleteException();
  }
}
