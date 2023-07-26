/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.data.DiskMetrics;

import java.util.*;

/** maps longs to lists of regions */
public class Index {
  private final HashMap<Long, ArrayList<AnnotatedRegion>> index;

  public Index() {
    this.index = new HashMap<>();
  }

  /** append a region to an id */
  public int append(long id, AnnotatedRegion region) {
    ArrayList<AnnotatedRegion> regions = index.get(id);
    if (regions == null) {
      regions = new ArrayList<>();
      index.put(id, regions);
    }
    regions.add(region);
    return regions.size();
  }

  /** return the regions bound to an object */
  public Iterator<AnnotatedRegion> get(long id) {
    List<AnnotatedRegion> regions = index.get(id);
    if (regions == null) {
      regions = Collections.emptyList();
    }
    return regions.iterator();
  }

  /** report on what is within the index */
  public void report(DiskMetrics metrics) {
    metrics.active_entries.set(index.size());
    int over_10K = 0;
    int over_20K = 0;
    int over_50K = 0;
    int over_100K = 0;
    for (List<AnnotatedRegion> regions : index.values()) {
      if (regions.size() > 10000) {
        over_10K++;
      }
      if (regions.size() > 20000) {
        over_20K++;
      }
      if (regions.size() > 50000) {
        over_50K++;
      }
      if (regions.size() > 100000) {
        over_100K++;
      }
    }
    metrics.items_over_tenk.set(over_10K);
    metrics.items_over_twentyk.set(over_20K);
    metrics.items_over_fiftyk.set(over_50K);
    metrics.items_over_onehundredk.set(over_100K);
  }

  public Set<Long> list() {
    return index.keySet();
  }

  /** does the index contain the given id */
  public boolean exists(long id) {
    return index.containsKey(id);
  }

  /** delete an object by id; return the regions allocated to it */
  public ArrayList<AnnotatedRegion> delete(long id) {
    return index.remove(id);
  }

  /** trim the head of an object (by id) the given maximum size; returned the returned regions */
  public ArrayList<AnnotatedRegion> trim(long id, int maxSize) {
    ArrayList<AnnotatedRegion> regions = index.get(id);
    if (regions != null && regions.size() > maxSize) {
      ArrayList<AnnotatedRegion> trimmed = new ArrayList<>();
      Iterator<AnnotatedRegion> it = regions.iterator();
      int count = regions.size() - maxSize;
      int k = 0;
      while (k < count && it.hasNext()) {
        AnnotatedRegion region = it.next();
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
    for (Map.Entry<Long, ArrayList<AnnotatedRegion>> entry : index.entrySet()) {
      buf.writeBoolean(true);
      buf.writeLongLE(entry.getKey());
      buf.writeIntLE(entry.getValue().size());
      for (AnnotatedRegion region : entry.getValue()) {
        buf.writeLongLE(region.position);
        buf.writeIntLE(region.size);
        buf.writeIntLE(region.seq);
        buf.writeLongLE(region.assetBytes);
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
      ArrayList<AnnotatedRegion> regions = new ArrayList<>(count);
      for (int k = 0; k < count; k++) {
        long start = buf.readLongLE();
        int size = buf.readIntLE();
        int seq = buf.readIntLE();
        long assetBytes = buf.readLongLE();
        AnnotatedRegion region = new AnnotatedRegion(start, size, seq, assetBytes);
        regions.add(region);
      }
      index.put(id, regions);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Long, ArrayList<AnnotatedRegion>> entry : index.entrySet()) {
      sb.append(entry.getKey()).append("=");
      for (AnnotatedRegion region : entry.getValue()) {
        sb.append(region.toString());
      }
      sb.append(";");
    }
    return sb.toString();
  }
}
