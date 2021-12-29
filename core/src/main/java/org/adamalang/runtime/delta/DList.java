/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.delta;

import java.util.ArrayList;
import java.util.function.Supplier;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

/** a list that will respect privacy and sends state to client only on changes */
public class DList<dTy> {
  public final ArrayList<dTy> cachedDeltas;
  private int emittedSize;

  public DList() {
    this.cachedDeltas = new ArrayList<>();
    this.emittedSize = 0;
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
    // Note: this function is called by generated code (See CodeGenDeltaClass) for each item in the array, and the value
    // will be written out per item if there is something.
    while (cachedDeltas.size() <= k) {
      cachedDeltas.add(maker.get());
    }
    return cachedDeltas.get(k);
  }

  /** rectify the size */
  public void rectify(final int size, final PrivateLazyDeltaWriter writer) {
    // Note; this is called after the iteration to null out items
    for (var k = size; k < cachedDeltas.size(); k++) {
      writer.planField("" + k).writeNull();
    }
    if (emittedSize != cachedDeltas.size()) {
      writer.planField("@s").writeInt(size);
      emittedSize = cachedDeltas.size();
    }
  }
}
