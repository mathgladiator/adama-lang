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

import org.adamalang.caravan.index.Region;

import java.io.IOException;

/** simple interface for reading and writing bytes from a storage device (synchronous) */
public interface Storage {

  /** how much is allocated */
  long size();

  /** write some bytes to the given region */
  void write(Region region, byte[] mem);

  /** read a byte array from the given region */
  byte[] read(Region region);

  /** flush all writes to disk */
  void flush() throws IOException;

  /** close the storage */
  void close() throws IOException;
}
