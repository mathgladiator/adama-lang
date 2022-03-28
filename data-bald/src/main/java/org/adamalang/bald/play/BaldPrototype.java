/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.play;

import org.adamalang.bald.organization.Heap;
import org.adamalang.bald.organization.Index;
import org.adamalang.bald.organization.Region;

import java.util.ArrayList;

public class BaldPrototype {

  public Heap heap;
  public Index index;

  public BaldPrototype(Heap heap, Index index) {
    this.heap = heap;
    this.index = index;
  }

  public int append(long id, byte[] memory) {
    Region region = heap.ask(memory.length);
    int sz = index.append(id, region);
    // write APPEND:id,memory.length,region.start,bytes to WAL
    // ID:8,memory.length:4,START:8,region
    // write to disk
    return sz;
  }

  public void trim(long id, int writes) {
    ArrayList<Region> regionsToTrim = index.trim(id, writes);
    // write TRIMs to log
    for (Region region : regionsToTrim) {
      heap.free(region);
    }
  }
}
