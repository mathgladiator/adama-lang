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
package org.adamalang.caravan.data;

import org.adamalang.caravan.index.Region;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class MemoryMappedFileStorageTests {

  @Test
  public void flow() throws Exception {
    File fileToUse = File.createTempFile("adama_", "storage");
    MemoryMappedFileStorage storage = new MemoryMappedFileStorage(fileToUse, 8196);
    Assert.assertEquals(8196, storage.size());
    storage.write(new Region(8, 2), "Hi".getBytes(StandardCharsets.UTF_8));
    byte[] read = storage.read(new Region(8, 2));
    Assert.assertEquals("Hi", new String(read, StandardCharsets.UTF_8));
    storage.flush();
    storage.close();
    fileToUse.delete();
  }
}
