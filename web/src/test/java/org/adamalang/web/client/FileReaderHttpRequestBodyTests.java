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

import java.io.File;
import java.nio.file.Files;

public class FileReaderHttpRequestBodyTests {
  @Test
  public void flow() throws Exception {
    File temp = File.createTempFile("ADAMA_TEST", "SUIF");
    try {
      Files.writeString(temp.toPath(), "Hello World");
      FileReaderHttpRequestBody body = new FileReaderHttpRequestBody(temp);
      Assert.assertEquals(11, body.size);
      Assert.assertEquals("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e", body.sha256);
      Assert.assertEquals(11, body.size());
      Assert.assertEquals(5, body.read(new byte[5]));
      Assert.assertEquals(4, body.read(new byte[4]));
      Assert.assertEquals(2, body.read(new byte[5]));
      body.finished(true);
    } finally {
      temp.delete();
    }
  }
}
