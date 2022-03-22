package org.adamalang.bald.organization;

import io.netty.buffer.ByteBuf;
import org.adamalang.bald.organization.Heap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Index {
  public HashMap<Long, ArrayList<Heap.Region>> index;

  public int append(long id, Heap.Region region) {
    ArrayList<Heap.Region> regions = index.get(id);
    if (regions == null) {
      regions = new ArrayList<>();
      index.put(id, regions);
    }
    regions.add(region);
    return regions.size();
  }

  public ArrayList<Heap.Region> trim(long id, int count) {
    ArrayList<Heap.Region> trimmed = new ArrayList<>();
    ArrayList<Heap.Region> regions = index.get(id);
    if (regions != null) {
      Iterator<Heap.Region> it = regions.iterator();
      int k = 0;
      while (k < count && it.hasNext()) {
        Heap.Region region = it.next();
        trimmed.add(region);
        it.remove();
      }
    }
    return trimmed;
  }

  public void snapshot(ByteBuf buf) {
    for (Map.Entry<Long, ArrayList<Heap.Region>> entry : index.entrySet()) {
      buf.writeBoolean(true);
      buf.writeLongLE(entry.getKey());
      buf.writeIntLE(entry.getValue().size());
      for (Heap.Region region: entry.getValue()) {
        buf.writeLongLE(region.position);
        buf.writeIntLE(region.size);
      }
    }
    buf.writeBoolean(false);
  }

  public void load(ByteBuf buf) {
    index.clear();
    while (buf.readBoolean()) {
      long id = buf.readLongLE();
      int count = buf.readIntLE();
      ArrayList<Heap.Region> regions = new ArrayList<>(count);
      for (int k = 0; k < count; k++) {
        long start = buf.readLongLE();
        int size = buf.readIntLE();
        Heap.Region region = new Heap.Region(start, size);
        regions.add(region);
      }
      index.put(id, regions);
    }
  }
}
