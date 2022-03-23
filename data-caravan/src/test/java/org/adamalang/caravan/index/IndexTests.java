package org.adamalang.caravan.index;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

public class IndexTests {
  public void assertEquals(String expected, Index index) {
    Assert.assertEquals(expected, index.toString());
    ByteBuf buf = Unpooled.buffer();
    index.snapshot(buf);
    Index index2 = new Index();
    index2.load(buf);
    Assert.assertEquals(expected, index2.toString());
  }

  @Test
  public void flow() {
    Index index = new Index();
    assertEquals("", index);
    Heap heap = new Heap(1024);

    index.append(1L, heap.ask(100));
    index.append(1L, heap.ask(100));
    assertEquals("1=[0,100)[100,200);", index);
    index.append(2L, heap.ask(100));
    index.append(2L, heap.ask(100));
    assertEquals("1=[0,100)[100,200);2=[200,300)[300,400);", index);
    index.append(3L, heap.ask(100));
    index.append(3L, heap.ask(100));
    assertEquals("1=[0,100)[100,200);2=[200,300)[300,400);3=[400,500)[500,600);", index);

    index.append(4L, heap.ask(1));
    index.append(4L, heap.ask(2));
    index.append(4L, heap.ask(3));
    index.append(4L, heap.ask(4));
    assertEquals("1=[0,100)[100,200);2=[200,300)[300,400);3=[400,500)[500,600);4=[600,601)[601,603)[603,606)[606,610);", index);

    for (Region region : index.trim(4L, 3)) {
      heap.free(region);
    }
    assertEquals("1=[0,100)[100,200);2=[200,300)[300,400);3=[400,500)[500,600);4=[606,610);", index);

    Assert.assertTrue(index.exists(3));
    for (Region region : index.delete(3)) {
      heap.free(region);
    }
    Assert.assertFalse(index.exists(3));
    assertEquals("1=[0,100)[100,200);2=[200,300)[300,400);4=[606,610);", index);

    for (Region region : index.delete(4)) {
      heap.free(region);
    }
    for (Region region : index.delete(2)) {
      heap.free(region);
    }
    for (Region region : index.delete(1)) {
      heap.free(region);
    }
    assertEquals("", index);
    Assert.assertEquals("[0,1024)", heap.toString());
    Assert.assertNull(index.trim(500, 1));
  }
}
