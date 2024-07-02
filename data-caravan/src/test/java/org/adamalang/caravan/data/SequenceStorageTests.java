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

public class SequenceStorageTests {

  @Test
  public void flow() throws Exception {
    File fileToUse1 = File.createTempFile("adama_", "storage1");
    File fileToUse2 = File.createTempFile("adama_", "storage2");
    MemoryMappedFileStorage storage1 = new MemoryMappedFileStorage(fileToUse1, 512);
    MemoryMappedFileStorage storage2 = new MemoryMappedFileStorage(fileToUse2, 8196);
    SequenceStorage storage = new SequenceStorage(storage1, storage2);
    Assert.assertEquals(8196 + 512, storage.size());
    {
      storage.write(new Region(8, 2), "Hi".getBytes(StandardCharsets.UTF_8));
      byte[] read = storage.read(new Region(8, 2));
      Assert.assertEquals("Hi", new String(read, StandardCharsets.UTF_8));
    }
    {
      storage.write(new Region(1024, 2), "Hi".getBytes(StandardCharsets.UTF_8));
      byte[] read = storage.read(new Region(1024, 2));
      Assert.assertEquals("Hi", new String(read, StandardCharsets.UTF_8));
    }
    Assert.assertNull(storage.read(new Region(100000, 2)));
    storage.flush();
    storage.close();
    fileToUse1.delete();
    fileToUse2.delete();
  }
}
