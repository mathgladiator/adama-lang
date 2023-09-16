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
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

public class LibAdamaTests {
  @Test
  public void flow_problems() {
    Assert.assertEquals("null", LibAdama.reflect("{").json);
    Assert.assertEquals("[{\"error\":true}]", LibAdama.validate("{").json);
    Assert.assertEquals("[]", LibAdama.validate("").json);
    Assert.assertEquals("null", LibAdama.reflect("int x = \"x\"").json);
    Assert.assertEquals("[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":3,\"byte\":3}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'r<int>' is unable to store type 'string'.\",\"file\":\"<direct code>\"}]", LibAdama.validate("int x = \"x\";").json);
    Assert.assertEquals("{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}}},\"channels\":{},\"channels-privacy\":{},\"constructors\":[],\"labels\":[]}", LibAdama.reflect("").json);
  }
}
