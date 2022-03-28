/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.index.Region;
import org.junit.Assert;
import org.junit.Test;

public class SplitHeapTests {

  private SplitHeat make() {
    return new SplitHeat(new IndexedHeap(128), 16, 128, new IndexedHeap(1024));
  }

  private void assetEqualsAfterSnapshot(String expected, SplitHeat heap) {
    Assert.assertEquals(expected, heap.toString());
    ByteBuf buf = Unpooled.buffer();
    heap.snapshot(buf);
    SplitHeat heap2 = make();
    heap2.load(buf);
    Assert.assertEquals(expected, heap2.toString());
  }

  @Test
  public void flow() throws Exception {
    SplitHeat heap = make();
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[0,1024)}", heap);
    Region a1 = heap.ask(7);
    assetEqualsAfterSnapshot("Split{left=[7,128),+offset128:right=[0,1024)}", heap);
    Region a2 = heap.ask(76);
    assetEqualsAfterSnapshot("Split{left=[7,128),+offset128:right=[76,1024)}", heap);
    Region a3 = heap.ask(2);
    assetEqualsAfterSnapshot("Split{left=[9,128),+offset128:right=[76,1024)}", heap);
    Region a4 = heap.ask(12);
    assetEqualsAfterSnapshot("Split{left=[21,128),+offset128:right=[76,1024)}", heap);
    heap.free(a2);
    assetEqualsAfterSnapshot("Split{left=[21,128),+offset128:right=[0,1024)}", heap);
    Region r1 = heap.ask(5);
    assetEqualsAfterSnapshot("Split{left=[26,128),+offset128:right=[0,1024)}", heap);
    Region r2 = heap.ask(50);
    assetEqualsAfterSnapshot("Split{left=[26,128),+offset128:right=[50,1024)}", heap);
    heap.free(r1);
    assetEqualsAfterSnapshot("Split{left=[21,128),+offset128:right=[50,1024)}", heap);
    Region az = heap.ask(15);
    assetEqualsAfterSnapshot("Split{left=[36,128),+offset128:right=[50,1024)}", heap);
    heap.free(a1);
    assetEqualsAfterSnapshot("Split{left=[0,7)[36,128),+offset128:right=[50,1024)}", heap);
    heap.free(a3);
    assetEqualsAfterSnapshot("Split{left=[0,9)[36,128),+offset128:right=[50,1024)}", heap);
    heap.free(a4);
    assetEqualsAfterSnapshot("Split{left=[0,21)[36,128),+offset128:right=[50,1024)}", heap);
    heap.free(r2);
    assetEqualsAfterSnapshot("Split{left=[0,21)[36,128),+offset128:right=[0,1024)}", heap);
    heap.free(az);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[0,1024)}", heap);
    Region x0 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,1024)}", heap);
    heap.free(x0);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[0,1024)}", heap);
    Region y0 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,1024)}", heap);
    Region y1 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[200,1024)}", heap);
    Region y2 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[300,1024)}", heap);
    Region y3 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[400,1024)}", heap);
    heap.free(y3);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[300,1024)}", heap);
    heap.free(y1);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,200)[300,1024)}", heap);
    heap.free(y2);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,1024)}", heap);
    heap.free(y0);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[0,1024)}", heap);
    Region k0 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,1024)}", heap);
    Region k1 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[200,1024)}", heap);
    Region k2 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[300,1024)}", heap);
    Region k3 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[400,1024)}", heap);
    Region k4 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[500,1024)}", heap);
    Region k5 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[600,1024)}", heap);
    Region k6 = heap.ask(100);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[700,1024)}", heap);
    heap.free(k1);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,200)[700,1024)}", heap);
    heap.free(k3);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,200)[300,400)[700,1024)}", heap);
    heap.free(k5);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,200)[300,400)[500,600)[700,1024)}", heap);
    heap.free(k6);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,200)[300,400)[500,1024)}", heap);
    heap.free(k4);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,200)[300,1024)}", heap);
    heap.free(k2);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[100,1024)}", heap);
    heap.free(k0);
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[0,1024)}", heap);
    Assert.assertNull(heap.ask(2048));
    assetEqualsAfterSnapshot("Split{left=[0,128),+offset128:right=[0,1024)}", heap);
  }
}
