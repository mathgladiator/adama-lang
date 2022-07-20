/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.index.Region;
import org.junit.Assert;
import org.junit.Test;

public class SequenceHeapTests {

  private SequenceHeap make() {
    return new SequenceHeap(new LimitHeap(new IndexedHeap(128), 8), new LimitHeap(new IndexedHeap(128), 16), new IndexedHeap(1024));
  }

  private void assetEqualsAfterSnapshot(String expected, SequenceHeap heap) {
    Assert.assertEquals(expected, heap.toString());
    ByteBuf buf = Unpooled.buffer();
    heap.snapshot(buf);
    SequenceHeap heap2 = make();
    heap2.load(buf);
    Assert.assertEquals(expected, heap2.toString());
  }

  @Test
  public void flow() throws Exception {
    SequenceHeap heap = make();
    Assert.assertEquals(1280, heap.max());
    Assert.assertEquals(1280, heap.available());
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [0,1024)]}", heap);
    Region a1 = heap.ask(7);
    Assert.assertEquals(1280 - 7, heap.available());
    assetEqualsAfterSnapshot("Seq{[[7,128), [0,128), [0,1024)]}", heap);
    Region a2 = heap.ask(76);
    Assert.assertEquals(1280 - 7 - 76, heap.available());
    assetEqualsAfterSnapshot("Seq{[[7,128), [0,128), [76,1024)]}", heap);
    Region a3 = heap.ask(2);
    assetEqualsAfterSnapshot("Seq{[[9,128), [0,128), [76,1024)]}", heap);
    Region a4 = heap.ask(12);
    assetEqualsAfterSnapshot("Seq{[[9,128), [12,128), [76,1024)]}", heap);
    heap.free(a2);
    assetEqualsAfterSnapshot("Seq{[[9,128), [12,128), [0,1024)]}", heap);
    Region r1 = heap.ask(5);
    assetEqualsAfterSnapshot("Seq{[[14,128), [12,128), [0,1024)]}", heap);
    Region r2 = heap.ask(50);
    assetEqualsAfterSnapshot("Seq{[[14,128), [12,128), [50,1024)]}", heap);
    heap.free(r1);
    assetEqualsAfterSnapshot("Seq{[[9,128), [12,128), [50,1024)]}", heap);
    Region az = heap.ask(15);
    assetEqualsAfterSnapshot("Seq{[[9,128), [27,128), [50,1024)]}", heap);
    heap.free(a1);
    assetEqualsAfterSnapshot("Seq{[[0,7)[9,128), [27,128), [50,1024)]}", heap);
    heap.free(a3);
    assetEqualsAfterSnapshot("Seq{[[0,128), [27,128), [50,1024)]}", heap);
    heap.free(a4);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,12)[27,128), [50,1024)]}", heap);
    heap.free(r2);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,12)[27,128), [0,1024)]}", heap);
    heap.free(az);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [0,1024)]}", heap);
    Region x0 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [100,1024)]}", heap);
    heap.free(x0);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [0,1024)]}", heap);
    Region y0 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [100,1024)]}", heap);
    Region y1 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [200,1024)]}", heap);
    Region y2 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [300,1024)]}", heap);
    Region y3 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [400,1024)]}", heap);
    heap.free(y3);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [300,1024)]}", heap);
    heap.free(y1);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [100,200)[300,1024)]}", heap);
    heap.free(y2);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [100,1024)]}", heap);
    heap.free(y0);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [0,1024)]}", heap);
    Region k0 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [100,1024)]}", heap);
    Region k1 = heap.ask(1);
    assetEqualsAfterSnapshot("Seq{[[1,128), [0,128), [100,1024)]}", heap);
    Region k2 = heap.ask(17);
    assetEqualsAfterSnapshot("Seq{[[1,128), [0,128), [117,1024)]}", heap);
    Region k3 = heap.ask(32);
    assetEqualsAfterSnapshot("Seq{[[1,128), [0,128), [149,1024)]}", heap);
    Region k4 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[1,128), [0,128), [249,1024)]}", heap);
    Region k5 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[1,128), [0,128), [349,1024)]}", heap);
    Region k6 = heap.ask(100);
    assetEqualsAfterSnapshot("Seq{[[1,128), [0,128), [449,1024)]}", heap);
    heap.free(k1);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [449,1024)]}", heap);
    heap.free(k3);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [117,149)[449,1024)]}", heap);
    heap.free(k5);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [117,149)[249,349)[449,1024)]}", heap);
    heap.free(k6);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [117,149)[249,1024)]}", heap);
    heap.free(k4);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [117,1024)]}", heap);
    heap.free(k2);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [100,1024)]}", heap);
    heap.free(k0);
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [0,1024)]}", heap);
    Assert.assertNull(heap.ask(2048));
    assetEqualsAfterSnapshot("Seq{[[0,128), [0,128), [0,1024)]}", heap);
  }
}
