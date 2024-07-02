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

import java.io.IOException;

/** combine multiple storages into one */
public class SequenceStorage implements Storage {
  private final Storage[] storages;
  private final long size;

  public SequenceStorage(Storage... storages) {
    this.storages = storages;
    long _size = 0;
    for (Storage storage : storages) {
      _size += storage.size();
    }
    this.size = _size;
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public void write(Region region, byte[] mem) {
    long at = region.position;
    for (Storage storage : storages) {
      if (at < storage.size()) {
        storage.write(new Region(at, region.size), mem);
        return;
      }
      at -= storage.size();
    }
  }

  @Override
  public byte[] read(Region region) {
    long at = region.position;
    for (Storage storage : storages) {
      if (at < storage.size()) {
        return storage.read(new Region(at, region.size));
      }
      at -= storage.size();
    }
    return null;
  }

  @Override
  public void flush() throws IOException {
    for (Storage storage : storages) {
      storage.flush();
    }
  }

  @Override
  public void close() throws IOException {
    for (Storage storage : storages) {
      storage.close();
    }
  }
}
