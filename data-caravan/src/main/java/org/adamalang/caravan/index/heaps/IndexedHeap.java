package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Region;

import java.util.*;

public class IndexedHeap implements Heap {
  public final long maximumSize;

  private final HashMap<Long, FreeSpace> left;
  private final HashMap<Long, FreeSpace> right;
  private final TreeMap<Long, TreeMap<Long, FreeSpace>> sized;

  /** a mapping of free space */
  private class FreeSpace implements Comparable<FreeSpace> {
    private long start;
    private long size;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      FreeSpace freeSpace = (FreeSpace) o;
      return start == freeSpace.start && size == freeSpace.size;// && Objects.equals(prior, freeSpace.prior) && Objects.equals(next, freeSpace.next);
    }

    @Override
    public int hashCode() {
      return Objects.hash(start, size);
    }

    @Override
    public String toString() {
      return "[" + start + "," + (start + size) + ")";
    }

    @Override
    public int compareTo(FreeSpace o) {
      return Long.compare(start, o.start);
    }
  }

  /** construct the heap as empty with the given maximum size available */
  public IndexedHeap(long maximumSize) {
    this.maximumSize = maximumSize;
    this.left = new HashMap<>();
    this.right = new HashMap<>();
    this.sized = new TreeMap<>();
    FreeSpace head = new FreeSpace();
    head.start = 0;
    head.size = maximumSize;
    add(head);
  }

  @Override
  public Region ask(int size) {
    Map.Entry<Long, TreeMap<Long, FreeSpace>> bucket = sized.ceilingEntry((long) size);
    if (bucket == null) {
      return null;
    }
    Map.Entry<Long, FreeSpace> first = bucket.getValue().firstEntry();
    FreeSpace space = first.getValue();
    remove(space);

    Region region = new Region(space.start, size);
    space.start += size;
    space.size -= size;
    add(space);
    return region;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    TreeSet<FreeSpace> items = new TreeSet<>(left.values());
    for (FreeSpace current : items) {
      sb.append(current.toString());
    }
    return sb.toString();
  }

  private void add(FreeSpace space) {
    left.put(space.start, space);
    right.put(space.start + space.size, space);
    TreeMap<Long, FreeSpace> bucket = sized.get(space.size);
    if (bucket == null) {
      bucket = new TreeMap<>();
      sized.put(space.size, bucket);
    }
    bucket.put(space.start, space);
  }

  private void remove(FreeSpace space) {
    left.remove(space.start);
    right.remove(space.start + space.size);
    TreeMap<Long, FreeSpace> bucket = sized.get(space.size);
    bucket.remove(space.start);
    if (bucket.size() == 0) {
      sized.remove(space.size);
    }
  }

  @Override
  public void free(Region region) {
    FreeSpace byLeft = left.get(region.position + region.size);
    if (byLeft != null) {
      remove(byLeft);
    }
    FreeSpace byRight = right.get(region.position);
    if (byRight != null) {
      remove(byRight);
    }
    if (byLeft != null && byRight != null) {
      // ByRight + Region + ByLeft
      FreeSpace newOne = new FreeSpace();
      newOne.start = byRight.start;
      newOne.size = byRight.size + region.size + byLeft.size;
      add(newOne);
    } else if (byLeft != null && byRight == null) {
      // __ + Region + ByLeft
      byLeft.size += region.size;
      byLeft.start -= region.size;
      add(byLeft);
    } else if (byLeft == null && byRight != null) {
      // ByRight + Region + __
      byRight.size += region.size;
      add(byRight);
    } else {
      // __ + Region + __
      FreeSpace newOne = new FreeSpace();
      newOne.start = region.position;
      newOne.size = region.size;
      add(newOne);
    }
  }

  @Override
  public void snapshot(ByteBuf buf) {
    for (FreeSpace current : left.values()) {
      buf.writeBoolean(true);
      buf.writeLongLE(current.start);
      buf.writeLongLE(current.size);
    }
    buf.writeBoolean(false);
  }

  @Override
  public void load(ByteBuf buf) {
    left.clear();
    right.clear();
    sized.clear();
    while (buf.readBoolean()) {
      FreeSpace current = new FreeSpace();
      current.start = buf.readLongLE();
      current.size = buf.readLongLE();
      add(current);
    }
  }
}
