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
package org.adamalang.caravan.events;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/** convert a file into a list of writes */
public class RestoreLoader {
  public static ArrayList<byte[]> load(File file) throws Exception {
    ArrayList<byte[]> writes = new ArrayList<>();
    try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
      while (input.readBoolean()) {
        byte[] bytes = new byte[input.readInt()];
        input.readFully(bytes);
        writes.add(bytes);
      }
    }
    return writes;
  }
}
