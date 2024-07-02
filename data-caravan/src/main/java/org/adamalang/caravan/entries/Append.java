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
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.contracts.WALEntry;

public class Append implements WALEntry<Append> {
  public final long id;
  public final long position;
  public final byte[] bytes;
  public final int seq;
  public final long assetBytes;

  public Append(long id, long position, byte[] bytes, int seq, long assetBytes) {
    this.id = id;
    this.position = position;
    this.bytes = bytes;
    this.seq = seq;
    this.assetBytes = assetBytes;
  }

  public static Append readAfterTypeId(ByteBuf buf) {
    long id = buf.readLongLE();
    long position = buf.readLongLE();
    int size = buf.readIntLE();
    byte[] bytes = new byte[size];
    buf.readBytes(bytes);
    int seq = buf.readIntLE();
    long assetBytes = buf.readLongLE();
    return new Append(id, position, bytes, seq, assetBytes);
  }

  public void write(ByteBuf buf) {
    buf.writeByte(0x42);
    buf.writeLongLE(id);
    buf.writeLongLE(position);
    buf.writeIntLE(bytes.length);
    buf.writeBytes(bytes);
    buf.writeIntLE(seq);
    buf.writeLongLE(assetBytes);
  }
}
