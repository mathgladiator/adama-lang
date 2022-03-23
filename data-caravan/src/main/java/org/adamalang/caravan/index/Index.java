package org.adamalang.caravan.index;

import io.netty.buffer.ByteBuf;

import java.util.*;

/** maps longs to lists of regions */
public class Index {
  private final HashMap<Long, ArrayList<Region>> index;

  public Index() {
    this.index = new HashMap<>();
  }

  /** append a region to an id */
  public int append(long id, Region region) {
    ArrayList<Region> regions = index.get(id);
    if (regions == null) {
      regions = new ArrayList<>();
      index.put(id, regions);
    }
    regions.add(region);
    return regions.size();
  }

  public Iterator<Region> get(long id) {
    List<Region> regions = index.get(id);
    if (regions == null) {
      regions = Collections.emptyList();
    }
    return regions.iterator();
  }

  /** does the index contain the given id */
  public boolean exists(long id) {
    return index.containsKey(id);
  }

  public ArrayList<Region> delete(long id) {
    return index.remove(id);
  }

  /** trim the head of an object (by id) the given count; returned the returned regions */
  public ArrayList<Region> trim(long id, int count) {
    ArrayList<Region> regions = index.get(id);
    if (regions != null) {
      ArrayList<Region> trimmed = new ArrayList<>();
      Iterator<Region> it = regions.iterator();
      int k = 0;
      while (k < count && it.hasNext()) {
        Region region = it.next();
        trimmed.add(region);
        it.remove();
        k++;
      }
      return trimmed;
    }
    return null;
  }

  /** take a snapshot of the index */
  public void snapshot(ByteBuf buf) {
    for (Map.Entry<Long, ArrayList<Region>> entry : index.entrySet()) {
      buf.writeBoolean(true);
      buf.writeLongLE(entry.getKey());
      buf.writeIntLE(entry.getValue().size());
      for (Region region: entry.getValue()) {
        buf.writeLongLE(region.position);
        buf.writeIntLE(region.size);
      }
    }
    buf.writeBoolean(false);
  }

  /** load an index from a snapshot */
  public void load(ByteBuf buf) {
    index.clear();
    while (buf.readBoolean()) {
      long id = buf.readLongLE();
      int count = buf.readIntLE();
      ArrayList<Region> regions = new ArrayList<>(count);
      for (int k = 0; k < count; k++) {
        long start = buf.readLongLE();
        int size = buf.readIntLE();
        Region region = new Region(start, size);
        regions.add(region);
      }
      index.put(id, regions);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Long, ArrayList<Region>> entry : index.entrySet()) {
      sb.append(entry.getKey()).append("=");
      for (Region region : entry.getValue()) {
        sb.append(region.toString());
      }
      sb.append(";");
    }
    return sb.toString();
  }
}
