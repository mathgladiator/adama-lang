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
package org.adamalang.caravan.data;

import org.junit.Test;

import java.io.File;

public class DurableListStoreSizingTests {

  @Test
  public void large() throws Exception {
    File f = File.createTempFile("ADAMATEST", "XYZ");
    DurableListStoreSizing sz = new DurableListStoreSizing(10L * 1024 * 1024 * 1024L, f);
    sz.storage.close();
    for (File x : f.getParentFile().listFiles()) {
      if (x.getName().startsWith("ADAMA")) {
        x.deleteOnExit();
      }
    }
  }
}
