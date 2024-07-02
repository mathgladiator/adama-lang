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

import java.util.ArrayList;

public class ExceptionCallbackTests {
  @Test
  public void flow() {
    ArrayList<String> v = new ArrayList<>();
    ExceptionCallback<String> cb = new ExceptionCallback<String>() {
      @Override
      public void invoke(String value) throws ErrorCodeException {
        if ("x".equals(value)) {
          throw new ErrorCodeException(1);
        }
        v.add(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        v.add("ex:" + ex.code);
      }
    };
    cb.success("yay");
    cb.failure(new ErrorCodeException(123));
    cb.success("x");
    Assert.assertEquals(3, v.size());
    Assert.assertEquals("yay", v.get(0));
    Assert.assertEquals("ex:123", v.get(1));
    Assert.assertEquals("ex:1", v.get(2));
  }
}
