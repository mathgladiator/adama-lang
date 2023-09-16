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
package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Region;
import org.adamalang.caravan.index.Report;
import org.junit.Assert;
import org.junit.Test;

public class IndexedHeapTests {

  private void assetEqualsAfterSnapshot(String expected, IndexedHeap heap) {
    Assert.assertEquals(expected, heap.toString());
    ByteBuf buf = Unpooled.buffer();
    heap.snapshot(buf);
    IndexedHeap heap2 = new IndexedHeap(heap.maximumSize);
    heap2.load(buf);
    Assert.assertEquals(expected, heap2.toString());
  }

  @Test
  public void full() {
  }

  @Test
  public void flow() throws Exception {
    IndexedHeap heap = new IndexedHeap(1024);
    Assert.assertEquals(1024, heap.max());
    assetEqualsAfterSnapshot("[0,1024)", heap);
    Assert.assertEquals(1024, heap.available());
    Region a1 = heap.ask(7);
    Assert.assertEquals(1017, heap.available());
    assetEqualsAfterSnapshot("[7,1024)", heap);
    Region a2 = heap.ask(76);
    Assert.assertEquals(941, heap.available());
    assetEqualsAfterSnapshot("[83,1024)", heap);
    Region a3 = heap.ask(2);
    assetEqualsAfterSnapshot("[85,1024)", heap);
    Region a4 = heap.ask(12);
    assetEqualsAfterSnapshot("[97,1024)", heap);
    heap.free(a2);
    assetEqualsAfterSnapshot("[7,83)[97,1024)", heap);
    Region r1 = heap.ask(5);
    {
      Report report = new Report();
      heap.report(report);
      Assert.assertEquals(1024, report.getTotalBytes());
      Assert.assertEquals(998, report.getFreeBytesAvailable());
    }
    assetEqualsAfterSnapshot("[12,83)[97,1024)", heap);
    Region r2 = heap.ask(50);
    assetEqualsAfterSnapshot("[62,83)[97,1024)", heap);
    heap.free(r1);
    assetEqualsAfterSnapshot("[7,12)[62,83)[97,1024)", heap);
    Region az = heap.ask(15);
    assetEqualsAfterSnapshot("[7,12)[77,83)[97,1024)", heap);
    {
      Report report = new Report();
      heap.report(report);
      Assert.assertEquals(1024, report.getTotalBytes());
      Assert.assertEquals(938, report.getFreeBytesAvailable());
    }
    heap.free(a1);
    assetEqualsAfterSnapshot("[0,12)[77,83)[97,1024)", heap);
    heap.free(a3);
    assetEqualsAfterSnapshot("[0,12)[77,85)[97,1024)", heap);
    heap.free(a4);
    assetEqualsAfterSnapshot("[0,12)[77,1024)", heap);
    heap.free(r2);
    assetEqualsAfterSnapshot("[0,62)[77,1024)", heap);
    heap.free(az);
    assetEqualsAfterSnapshot("[0,1024)", heap);
    Region x0 = heap.ask(100);
    assetEqualsAfterSnapshot("[100,1024)", heap);
    heap.free(x0);
    assetEqualsAfterSnapshot("[0,1024)", heap);
    Region y0 = heap.ask(100);
    assetEqualsAfterSnapshot("[100,1024)", heap);
    Region y1 = heap.ask(100);
    assetEqualsAfterSnapshot("[200,1024)", heap);
    Region y2 = heap.ask(100);
    assetEqualsAfterSnapshot("[300,1024)", heap);
    Region y3 = heap.ask(100);
    assetEqualsAfterSnapshot("[400,1024)", heap);
    heap.free(y3);
    assetEqualsAfterSnapshot("[300,1024)", heap);
    heap.free(y1);
    assetEqualsAfterSnapshot("[100,200)[300,1024)", heap);
    heap.free(y2);
    assetEqualsAfterSnapshot("[100,1024)", heap);
    heap.free(y0);
    assetEqualsAfterSnapshot("[0,1024)", heap);
    Region k0 = heap.ask(100);
    assetEqualsAfterSnapshot("[100,1024)", heap);
    Region k1 = heap.ask(100);
    assetEqualsAfterSnapshot("[200,1024)", heap);
    Region k2 = heap.ask(100);
    assetEqualsAfterSnapshot("[300,1024)", heap);
    Region k3 = heap.ask(100);
    assetEqualsAfterSnapshot("[400,1024)", heap);
    Region k4 = heap.ask(100);
    assetEqualsAfterSnapshot("[500,1024)", heap);
    Region k5 = heap.ask(100);
    assetEqualsAfterSnapshot("[600,1024)", heap);
    Region k6 = heap.ask(100);
    assetEqualsAfterSnapshot("[700,1024)", heap);
    heap.free(k1);
    assetEqualsAfterSnapshot("[100,200)[700,1024)", heap);
    heap.free(k3);
    assetEqualsAfterSnapshot("[100,200)[300,400)[700,1024)", heap);
    heap.free(k5);
    assetEqualsAfterSnapshot("[100,200)[300,400)[500,600)[700,1024)", heap);
    heap.free(k6);
    assetEqualsAfterSnapshot("[100,200)[300,400)[500,1024)", heap);
    heap.free(k4);
    assetEqualsAfterSnapshot("[100,200)[300,1024)", heap);
    heap.free(k2);
    assetEqualsAfterSnapshot("[100,1024)", heap);
    heap.free(k0);
    assetEqualsAfterSnapshot("[0,1024)", heap);
    Assert.assertNull(heap.ask(2048));
    assetEqualsAfterSnapshot("[0,1024)", heap);
    {
      Report report = new Report();
      heap.report(report);
      Assert.assertEquals(1024, report.getTotalBytes());
      Assert.assertEquals(1024, report.getFreeBytesAvailable());
    }
  }
}
