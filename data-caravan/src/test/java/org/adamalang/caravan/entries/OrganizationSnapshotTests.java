/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.index.*;
import org.adamalang.caravan.index.heaps.IndexedHeap;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

public class OrganizationSnapshotTests {

  private AnnotatedRegion wrap(Region r) {
    return new AnnotatedRegion(r.position, r.size, 0, 0L);
  }

  @Test
  public void snapshot() {
    ByteBuf buf = Unpooled.buffer();
    final String givenHeap;
    final String givenIndex;
    final String givenKeymap;
    {
      Heap heap = new IndexedHeap(1024);
      Index index = new Index();
      KeyMap keymap = new KeyMap();
      keymap.apply(new MapKey(new Key("space", "key"), 1));
      index.append(1, wrap(heap.ask(42)));
      index.append(2, wrap(heap.ask(100)));
      index.append(2, wrap(heap.ask(100)));
      index.append(2, wrap(heap.ask(100)));
      index.append(3, wrap(heap.ask(50)));
      givenHeap = heap.toString();
      givenIndex = index.toString();
      givenKeymap = keymap.toString();
      OrganizationSnapshot snapshot = new OrganizationSnapshot(heap, index, keymap);
      snapshot.write(buf);
    }
    {
      Assert.assertEquals(0x57, buf.readByte());
      Heap heap = new IndexedHeap(1024);
      Index index = new Index();
      KeyMap keymap = new KeyMap();
      OrganizationSnapshot.populateAfterTypeId(buf, heap, index, keymap);
      Assert.assertEquals(givenHeap, heap.toString());
      Assert.assertEquals(givenIndex, index.toString());
      Assert.assertEquals(givenKeymap, keymap.toString());
    }
  }
}
