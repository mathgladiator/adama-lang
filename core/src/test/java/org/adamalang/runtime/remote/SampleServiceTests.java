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
package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class SampleServiceTests {
  @Test
  public void coverage() {
    SampleService ss = new SampleService();
    AtomicBoolean called = new AtomicBoolean(false);
    ss.request(NtPrincipal.NO_ONE, "method", "{}", new Callback<String>() {
      @Override
      public void success(String value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        called.set(true);
        Assert.assertEquals(888888, ex.code);
      }
    });
    Assert.assertTrue(called.get());
  }
}
