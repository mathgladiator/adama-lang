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
import org.adamalang.runtime.data.Key;

import java.nio.charset.StandardCharsets;

public class MapKey implements WALEntry<MapKey>  {
  public final byte[] space;
  public final byte[] key;
  public final int id;

  public MapKey(Key key, int id) {
    this.space = key.space.getBytes(StandardCharsets.UTF_8);
    this.key = key.key.getBytes(StandardCharsets.UTF_8);
    this.id = id;
  }

  private MapKey(byte[] space, byte[] key, int id) {
    this.space = space;
    this.key = key;
    this.id = id;
  }

  @Override
  public void write(ByteBuf buf) {
    buf.writeByte(0x30);
    buf.writeIntLE(space.length);
    buf.writeBytes(space);
    buf.writeIntLE(key.length);
    buf.writeBytes(key);
    buf.writeIntLE(id);
  }

  public static MapKey readAfterTypeId(ByteBuf buf) {
    int sizeSpace = buf.readIntLE();
    byte[] bytesSpace = new byte[sizeSpace];
    buf.readBytes(bytesSpace);
    int sizeKey = buf.readIntLE();
    byte[] bytesKey = new byte[sizeKey];
    buf.readBytes(bytesKey);
    int id = buf.readIntLE();
    return new MapKey(bytesSpace, bytesKey, id);
  }

  public Key of() {
    return new Key(new String(this.space, StandardCharsets.UTF_8), new String(this.key, StandardCharsets.UTF_8));
  }
}
