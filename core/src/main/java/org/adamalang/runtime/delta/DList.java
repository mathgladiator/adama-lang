/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
