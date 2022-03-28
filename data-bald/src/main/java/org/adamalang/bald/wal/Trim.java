/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.wal;

import io.netty.buffer.ByteBuf;
import org.adamalang.bald.contracts.WALEntry;
import org.adamalang.bald.organization.Region;

import java.util.ArrayList;

public class Trim implements WALEntry {
  private final long id;
  private final int count;
  private final ArrayList<Region> regions;
  private final boolean delete;

  public Trim(long id, int count, ArrayList<Region> regions, boolean delete) {
    this.id = id;
    this.count = count;
    this.regions = regions;
    this.delete = delete;
  }

  public void write(ByteBuf buf) {
    // Type ID
    buf.writeByte(0x13);
    buf.writeLongLE(id);
    buf.writeIntLE(count);
    buf.writeIntLE(regions.size());
    buf.writeBoolean(delete);
    for (Region region: regions) {
      buf.writeLongLE(region.position);
      buf.writeIntLE(region.size);
    }
  }

  public static Trim readAfterTypeId(ByteBuf buf) {
    long id = buf.readLongLE();
    int count = buf.readIntLE();
    int size = buf.readIntLE();
    boolean delete = buf.readBoolean();
    ArrayList<Region> regions = new ArrayList<>();
    for (int k = 0; k < size; k++) {
      long pos = buf.readLongLE();
      int sz = buf.readIntLE();
      regions.add(new Region(pos, sz));
    }
    return new Trim(id, count, regions, delete);
  }
}
