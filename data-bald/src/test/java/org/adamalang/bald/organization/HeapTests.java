package org.adamalang.bald.organization;

import org.junit.Assert;
import org.junit.Test;

public class HeapTests {
  @Test
  public void flow() throws Exception {
    Heap heap = new Heap(1024);
    heap.dump();
    Region a1 = heap.ask(7);
    heap.dump();
    Region a2 = heap.ask(76);
    heap.dump();
    Region a3 = heap.ask(2);
    heap.dump();
    Region a4 = heap.ask(12);
    heap.dump();
    Assert.assertEquals(0, a1.position);
    Assert.assertEquals(7, a2.position);
    Assert.assertEquals(83, a3.position);
    Assert.assertEquals(85, a4.position);

    heap.free(a2);
    heap.dump();
    Region r1 = heap.ask(5);
    heap.dump();
    Region r2 = heap.ask(50);
    heap.dump();
    Assert.assertEquals(7, r1.position);
    Assert.assertEquals(12, r2.position);

    heap.free(r1);
    heap.dump();
    heap.free(a1);
    heap.dump();
    heap.free(a3);
    heap.dump();
    heap.free(a4);
    heap.dump();
    heap.free(r2);
    heap.dump();
  }
}
