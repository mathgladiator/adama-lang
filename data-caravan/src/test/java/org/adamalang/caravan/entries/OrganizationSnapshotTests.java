/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.index.AnnotatedRegion;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Index;
import org.adamalang.caravan.index.Region;
import org.adamalang.caravan.index.heaps.IndexedHeap;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class OrganizationSnapshotTests {

  private AnnotatedRegion wrap(Region r) {
    return new AnnotatedRegion(r.position, r.size, 0, 0L);
  }

  @Test
  public void snapshot() {
    ByteBuf buf = Unpooled.buffer();
    final String givenHeap;
    final String givenIndex;
    {
      Heap heap = new IndexedHeap(1024);
      Index index = new Index();
      index.append(1, wrap(heap.ask(42)));
      index.append(2, wrap(heap.ask(100)));
      index.append(2, wrap(heap.ask(100)));
      index.append(2, wrap(heap.ask(100)));
      index.append(3, wrap(heap.ask(50)));
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
