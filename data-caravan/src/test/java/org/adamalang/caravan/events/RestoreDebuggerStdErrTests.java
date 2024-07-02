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
package org.adamalang.caravan.events;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;

public class RestoreDebuggerStdErrTests {
  @Test
  public void batch() {
    ArrayList<byte[]> writes = new ArrayList<>();
    Events.Batch batch = new Events.Batch();
    batch.changes = new Events.Change[2];
    batch.changes[0] = new Events.Change();
    batch.changes[0].seq_begin = 2;
    batch.changes[0].seq_end = 4;
    batch.changes[0].active = false;
    batch.changes[0].agent = "agent";
    batch.changes[0].authority = "authority";
    batch.changes[0].dAssetBytes = 100;
    batch.changes[0].redo = "{\"x\":2}";
    batch.changes[0].undo = "undo";

    batch.changes[1] = new Events.Change();
    batch.changes[1].seq_begin = 4;
    batch.changes[1].seq_end = 5;
    batch.changes[1].active = false;
    batch.changes[1].agent = "agent";
    batch.changes[1].authority = "authority";
    batch.changes[1].dAssetBytes = 100;
    batch.changes[1].redo = "{\"x\":3}";
    batch.changes[1].undo = "undo";

    ByteBuf buf = Unpooled.buffer();
    EventCodec.write(buf, batch);
    writes.add(ByteArrayHelper.convert(buf));
    RestoreDebuggerStdErr.print(writes);
  }
}
