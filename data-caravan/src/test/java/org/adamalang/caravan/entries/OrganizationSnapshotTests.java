/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Index;
import org.adamalang.caravan.index.heaps.IndexedHeap;
import org.junit.Assert;
import org.junit.Test;

public class OrganizationSnapshotTests {
  @Test
  public void snapshot() {
    ByteBuf buf = Unpooled.buffer();
    final String givenHeap;
    final String givenIndex;
    {
      Heap heap = new IndexedHeap(1024);
      Index index = new Index();
      index.append(1, heap.ask(42));
      index.append(2, heap.ask(100));
      index.append(2, heap.ask(100));
      index.append(2, heap.ask(100));
      index.append(3, heap.ask(50));
      givenHeap = heap.toString();
      givenIndex = index.toString();
      OrganizationSnapshot snapshot = new OrganizationSnapshot(heap, index);
      snapshot.write(buf);
    }
    {
      Assert.assertEquals(0x55, buf.readByte());
      Heap heap = new IndexedHeap(1024);
      Index index = new Index();
      OrganizationSnapshot.populateAfterTypeId(buf, heap, index);
      Assert.assertEquals(givenHeap, heap.toString());
      Assert.assertEquals(givenIndex, index.toString());
    }
  }
}
