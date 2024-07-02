/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
    int over_1M = 0;
    int total = 0;
    for (List<AnnotatedRegion> regions : index.values()) {
      for (AnnotatedRegion region : regions) {
        total++;
        if (region.size > 10000) {
          over_10K++;
        }
        if (region.size > 20000) {
          over_20K++;
        }
        if (region.size > 50000) {
          over_50K++;
        }
        if (region.size > 100000) {
          over_100K++;
        }
        if (region.size > 1000000) {
          over_1M++;
        }
      }
    }
    metrics.items_total.set(total);
    metrics.items_over_tenk.set(over_10K);
    metrics.items_over_twentyk.set(over_20K);
    metrics.items_over_fiftyk.set(over_50K);
    metrics.items_over_onehundredk.set(over_100K);
    metrics.items_over_onemega.set(over_1M);
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
