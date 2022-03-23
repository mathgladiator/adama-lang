package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Region;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TrimTests {
  @Test
  public void flow1() {
    ArrayList<Region> regions = new ArrayList<>();
    Heap heap = new Heap(1024);
    regions.add(heap.ask(12));
    regions.add(heap.ask(13));
    regions.add(heap.ask(14));
    Trim trim = new Trim(4, regions);
    ByteBuf buf = Unpooled.buffer();
    trim.write(buf);
    Assert.assertEquals(0x13, buf.readByte());
    Trim trim2 = Trim.readAfterTypeId(buf);
    Assert.assertEquals(4L, trim2.id);
    Assert.assertEquals("[39,1024)", heap.toString());
    for (Region region : trim2.regions) {
      heap.free(region);
    }
    Assert.assertEquals("[0,1024)", heap.toString());
  }

  @Test
  public void empty() {
    ArrayList<Region> regions = new ArrayList<>();
    Heap heap = new Heap(1024);
    Trim trim = new Trim(4, regions);
    ByteBuf buf = Unpooled.buffer();
    trim.write(buf);
    Assert.assertEquals(0x13, buf.readByte());
    Trim trim2 = Trim.readAfterTypeId(buf);
    Assert.assertEquals(4L, trim2.id);
    Assert.assertEquals("[0,1024)", heap.toString());
    for (Region region : trim2.regions) {
      heap.free(region);
    }
    Assert.assertEquals("[0,1024)", heap.toString());
  }
}
