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

import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.heaps.IndexedHeap;
import org.adamalang.caravan.index.heaps.LimitHeap;
import org.adamalang.caravan.index.heaps.SequenceHeap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/** Setup the heap and storage for very large data durable list store */
public class DurableListStoreSizing {
  private static final long SIZE_CUT_OFF = 1024 * 1024 * 1024;
  public final Heap heap;
  public final Storage storage;

  public DurableListStoreSizing(long totalSize, File base) throws IOException {
    ArrayList<Heap> heaps = new ArrayList<>();
    ArrayList<Storage> storages = new ArrayList<>();

    long size = totalSize;
    if (size >= SIZE_CUT_OFF) {
      heaps.add(new LimitHeap(new IndexedHeap(SIZE_CUT_OFF / 4), 8196));
      heaps.add(new LimitHeap(new IndexedHeap(SIZE_CUT_OFF / 4), 4 * 8196));
      heaps.add(new IndexedHeap(SIZE_CUT_OFF / 2));
      storages.add(new MemoryMappedFileStorage(new File(base.getParentFile(), base.getName() + "-PRIME"), SIZE_CUT_OFF));
      size -= SIZE_CUT_OFF;
    }
    int k = 0;
    while (size > 0) {
      long sizeToUse = size;
      if (sizeToUse > SIZE_CUT_OFF) {
        sizeToUse = SIZE_CUT_OFF;
      }
      size -= sizeToUse;
      heaps.add(new IndexedHeap(sizeToUse));
      storages.add(new MemoryMappedFileStorage(new File(base.getParentFile(), base.getName() + "-" + k), sizeToUse));
      k++;
    }

    this.heap = new SequenceHeap(heaps.toArray(new Heap[heaps.size()]));
    this.storage = new SequenceStorage(storages.toArray(new Storage[storages.size()]));
  }
}
