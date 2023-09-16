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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class XWWWFormUrlTests {
  @Test
  public void simple() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{}"));
    Assert.assertEquals("", result);
  }

  @Test
  public void one() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{\"x\":123}"));
    Assert.assertEquals("x=123", result);
  }

  @Test
  public void two() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{\"x\":123,\"y\":\"xyz\"}"));
    Assert.assertEquals("x=123&y=xyz", result);
  }

  @Test
  public void three() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{\"x\":4.2,\"y\":\"xyz\",\"z\":true}"));
    Assert.assertEquals("x=4.2&y=xyz&z=true", result);
  }

  @Test
  public void compound() {
    String result = XWWWFormUrl.encode(Json.parseJsonObject("{\"o\":{\"x\":4.2,\"y\":\"xyz\",\"z\":true}}"));
    Assert.assertEquals("o.x=4.2&o.y=xyz&o.z=true", result);
  }
}
