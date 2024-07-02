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
package org.adamalang.runtime.delta;

import org.adamalang.runtime.contracts.DeltaNode;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

import java.util.ArrayList;
import java.util.function.Supplier;

/** a list that will respect privacy and sends state to client only on changes */
public class DList<dTy extends DeltaNode> implements DeltaNode {
  public final ArrayList<dTy> cachedDeltas;
  private int emittedSize;

  public DList() {
    this.cachedDeltas = new ArrayList<>();
    this.emittedSize = 0;
  }

  @Override
  public void clear() {
    this.cachedDeltas.clear();
    this.emittedSize = 0;
  }

  /** memory usage */
  @Override
  public long __memory() {
    long memory = 128;
    for (dTy item : cachedDeltas) {
      memory += item.__memory();
    }
    return memory;
  }

  /** the list is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (emittedSize > 0) {
      emittedSize = 0;
      cachedDeltas.clear();
      writer.writeNull();
    }
  }

  /** get the prior value at the given index using the supplier to fill in holes */
  public dTy getPrior(final int k, final Supplier<dTy> maker) {
    // Note: this function is called by generated code (See CodeGenDeltaClass) for each item in the
    // array, and the value
    // will be written out per item if there is something.
    while (cachedDeltas.size() <= k) {
      cachedDeltas.add(maker.get());
    }
    return cachedDeltas.get(k);
  }

  /** rectify the size */
  public void rectify(final int size, final PrivateLazyDeltaWriter writer) {
    // Note; this is called after the iteration to null out items
    int oldSize = cachedDeltas.size();
    for (var k = size; k < oldSize; k++) {
      writer.planField("" + k).writeNull();
      cachedDeltas.remove(cachedDeltas.size() - 1);
    }
    if (emittedSize != cachedDeltas.size()) {
      writer.planField("@s").writeInt(size);
      emittedSize = cachedDeltas.size();
    }
  }
}
