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
package org.adamalang.web.client;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class SimpleHttpRequestBodyTests {
  @Test
  public void empty() throws Exception {
    Assert.assertEquals(0, SimpleHttpRequestBody.EMPTY.size());
    SimpleHttpRequestBody.EMPTY.read(null);
    SimpleHttpRequestBody.EMPTY.finished(true);
  }
  @Test
  public void bytearray() throws Exception {
    SimpleHttpRequestBody body = SimpleHttpRequestBody.WRAP("Hello World".getBytes(StandardCharsets.UTF_8));
    Assert.assertEquals(11, body.size());
    byte[] chunk = new byte[4];
    Assert.assertEquals(4, body.read(chunk));
    Assert.assertEquals("Hell", new String(chunk, StandardCharsets.UTF_8));
    Assert.assertEquals(4, body.read(chunk));
    Assert.assertEquals("o Wo", new String(chunk, StandardCharsets.UTF_8));
    Assert.assertEquals(3, body.read(chunk));
    Assert.assertEquals("rld", new String(chunk, 0, 3, StandardCharsets.UTF_8));
    body.finished(true);
  }
}
