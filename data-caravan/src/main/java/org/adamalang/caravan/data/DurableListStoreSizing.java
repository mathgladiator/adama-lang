/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
