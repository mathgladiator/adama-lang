/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.organization;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Index {
  public HashMap<Long, ArrayList<Region>> index;

  public int append(long id, Region region) {
    ArrayList<Region> regions = index.get(id);
    if (regions == null) {
      regions = new ArrayList<>();
      index.put(id, regions);
    }
    regions.add(region);
    return regions.size();
  }

  public ArrayList<Region> trim(long id, int count) {
    ArrayList<Region> trimmed = new ArrayList<>();
    ArrayList<Region> regions = index.get(id);
    if (regions != null) {
      Iterator<Region> it = regions.iterator();
      int k = 0;
      while (k < count && it.hasNext()) {
        Region region = it.next();
        trimmed.add(region);
        it.remove();
      }
    }
    return trimmed;
  }

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
}
